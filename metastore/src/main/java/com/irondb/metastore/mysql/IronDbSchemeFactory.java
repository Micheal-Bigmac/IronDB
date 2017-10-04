package com.irondb.metastore.mysql;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.exception.IronDBMetaException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.IllegalFormatException;
import java.util.List;


/**
 * 该类 是个工厂类 根据不同的 外部存储 产生对应的 connection  同时把文件中多行同属于一条Sql 转换成 一行一条SQL
 */
public class IronDbSchemeFactory {
    public static final String DB_DERBY = "derby";
    public static final String DB_HIVE = "hive";
    public static final String DB_MSSQL = "mssql";
    public static final String DB_MYSQL = "mysql";
    public static final String DB_POSTGRACE = "postgres";
    public static final String DB_ORACLE = "oracle";

    /**
     * Get JDBC connection to metastore db
     *
     * @param userName  metastore connection username
     * @param password  metastore connection password
     * @param printInfo print connection parameters
     * @param conf      hive config object
     * @return metastore connection object
     */
    public static Connection getConnectionToMetastore(String userName,
                                                      String password, String url, String driver, boolean printInfo)
            throws IronDBMetaException {
        try {
            // load required JDBC driver
            Class.forName(driver);

            return DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            throw new IronDBMetaException("Failed to get schema version.", e);
        } catch (ClassNotFoundException e) {
            throw new IronDBMetaException("Failed to load driver", e);
        }
    }

    public static Connection getConnectionToMetastore(MetaStoreConnectionInfo info) throws IronDBMetaException {
        return getConnectionToMetastore(info.getUsername(), info.getPassword(), info.getUrl(),
                info.getDriver(), info.getPrintInfo());
    }


    public interface NestedScriptParser {

        enum CommandType {
            PARTIAL_STATEMENT,
            TERMINATED_STATEMENT,
            COMMENT
        }

        String DEFAULT_DELIMITER = ";";
        String DEFAULT_QUOTE = "\"";

        /**
         * Find the type of given command
         *
         * @param dbCommand
         * @return
         */
//        boolean isPartialCommand(String dbCommand) throws IllegalArgumentException;

        /**
         * Parse the DB specific nesting format and extract the inner script name if any
         *
         * @param dbCommand command from parent script
         * @return
         * @throws IllegalFormatException
         */
        String getScriptName(String dbCommand) throws IllegalArgumentException;

        /**
         * Find if the given command is a nested script execution
         *
         * @param dbCommand
         * @return
         */
        boolean isNestedScript(String dbCommand);

        /**
         * Find if the given command should not be passed to DB
         *
         * @param dbCommand
         * @return
         */
        boolean isNonExecCommand(String dbCommand);

        /**
         * Get the SQL statement delimiter
         *
         * @return
         */
        String getDelimiter();

        /**
         * Get the SQL indentifier quotation character
         *
         * @return
         */
        String getQuoteCharacter();

        /**
         * Clear any client specific tags
         *
         * @return
         */
        String cleanseCommand(String dbCommand);

        /**
         * Does the DB required table/column names quoted
         *
         * @return
         */
        boolean needsQuotedIdentifier();

        /**
         * Flatten the nested upgrade script into a buffer
         *
         * @param scriptDir  upgrade script directory
         * @param scriptFile upgrade script file
         * @return string of sql commands
         */
        String buildCommand(String scriptDir, String scriptFile)
                throws IllegalFormatException, IOException;

        /**
         * Flatten the nested upgrade script into a buffer
         *
         * @param scriptDir  upgrade script directory
         * @param scriptFile upgrade script file
         * @param fixQuotes  whether to replace quote characters
         * @return string of sql commands
         */
        String buildCommand(String scriptDir, String scriptFile, boolean fixQuotes)
                throws IllegalFormatException, IOException;
    }

    /**
     * Base implementation of NestedScriptParser
     * abstractCommandParser.
     */
    private static abstract class AbstractCommandParser implements NestedScriptParser {
        private List<String> dbOpts;
        private String msUsername;
        private String msPassword;
        private IronDBContext conf;

        public AbstractCommandParser(String dbOpts, String msUsername, String msPassword,
                                     IronDBContext conf) {
            setDbOpts(dbOpts);
            this.msUsername = msUsername;
            this.msPassword = msPassword;
            this.conf = conf;
        }

        public boolean isPartialCommand(String dbCommand) throws IllegalArgumentException {
            if (dbCommand == null || "".equals(dbCommand)) {
                throw new IllegalArgumentException("invalid command line " + dbCommand);
            }
            dbCommand = dbCommand.trim();
            if (dbCommand.endsWith(getDelimiter()) || isNonExecCommand(dbCommand)) {
                return false;
            } else {
                return true;
            }
        }

        public boolean isNonExecCommand(String dbCommand) {
            return (dbCommand.startsWith("--") || dbCommand.startsWith("#"));
        }

        public String getDelimiter() {
            return DEFAULT_DELIMITER;
        }

        public String getQuoteCharacter() {
            return DEFAULT_QUOTE;
        }


        public String cleanseCommand(String dbCommand) {
            // strip off the delimiter
            if (dbCommand.endsWith(getDelimiter())) {
                dbCommand = dbCommand.substring(0,
                        dbCommand.length() - getDelimiter().length());
            }
            return dbCommand;
        }

        public boolean needsQuotedIdentifier() {
            return false;
        }

        public String buildCommand(
                String scriptDir, String scriptFile) throws IllegalFormatException, IOException {
            return buildCommand(scriptDir, scriptFile, false);
        }

        public String buildCommand(
                String scriptDir, String scriptFile, boolean fixQuotes) throws IllegalFormatException, IOException {
            BufferedReader bfReader =
                    new BufferedReader(new FileReader(scriptDir + File.separatorChar + scriptFile));
            String currLine;
            StringBuilder sb = new StringBuilder();
            String currentCommand = null;
            while ((currLine = bfReader.readLine()) != null) {
                currLine = currLine.trim();

                if (fixQuotes && !getQuoteCharacter().equals(DEFAULT_QUOTE)) {
                    currLine = currLine.replace("\\\"", getQuoteCharacter());
                }

                if ("".equals(currLine)) {
                    continue; // skip empty lines
                }

                if (currentCommand == null) {
                    currentCommand = currLine;
                } else {
                    currentCommand = currentCommand + " " + currLine;
                }
                if (isPartialCommand(currLine)) {
                    // if its a partial line, continue collecting the pieces
                    continue;
                }

                // if this is a valid executable command then add it to the buffer
                if (!isNonExecCommand(currentCommand)) {
                    currentCommand = cleanseCommand(currentCommand);
                    if (isNestedScript(currentCommand)) {
                        // if this is a nested sql script then flatten it
                        String currScript = getScriptName(currentCommand);
                        sb.append(buildCommand(scriptDir, currScript));
                    } else {
                        // Now we have a complete statement, process it
                        // write the line to buffer
                        sb.append(currentCommand);
                        sb.append(System.getProperty("line.separator"));
                    }
                }
                currentCommand = null;
            }
            bfReader.close();
            return sb.toString();
        }

        private void setDbOpts(String dbOpts) {
            if (dbOpts != null) {
                this.dbOpts = Lists.newArrayList(dbOpts.split(","));
            } else {
                this.dbOpts = Lists.newArrayList();
            }
        }

        protected List<String> getDbOpts() {
            return dbOpts;
        }

        protected String getMsUsername() {
            return msUsername;
        }

        protected String getMsPassword() {
            return msPassword;
        }

        protected IronDBContext getConf() {
            return conf;
        }
    }

    // Derby commandline parser
    public static class DerbyCommandParser extends AbstractCommandParser {
        private static final String DERBY_NESTING_TOKEN = "RUN";

        public DerbyCommandParser(String dbOpts, String msUsername, String msPassword,
                                  IronDBContext conf) {
            super(dbOpts, msUsername, msPassword, conf);
        }

        public String getScriptName(String dbCommand) throws IllegalArgumentException {

            if (!isNestedScript(dbCommand)) {
                throw new IllegalArgumentException("Not a script format " + dbCommand);
            }
            String[] tokens = dbCommand.split(" ");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Couldn't parse line " + dbCommand);
            }
            return tokens[1].replace(";", "").replaceAll("'", "");
        }

        public boolean isNestedScript(String dbCommand) {
            // Derby script format is RUN '<file>'
            return dbCommand.startsWith(DERBY_NESTING_TOKEN);
        }
    }

    // Derby commandline parser
    public static class HiveCommandParser extends AbstractCommandParser {
        private static String HIVE_NESTING_TOKEN = "SOURCE";
        private final NestedScriptParser nestedDbCommandParser;

        public HiveCommandParser(String dbOpts, String msUsername, String msPassword,
                                 IronDBContext conf, String metaDbType) {
            super(dbOpts, msUsername, msPassword, conf);
            nestedDbCommandParser = getDbCommandParser(metaDbType);
        }

        @Override
        public String getQuoteCharacter() {
            return nestedDbCommandParser.getQuoteCharacter();
        }

        public String getScriptName(String dbCommand) throws IllegalArgumentException {

            if (!isNestedScript(dbCommand)) {
                throw new IllegalArgumentException("Not a script format " + dbCommand);
            }
            String[] tokens = dbCommand.split(" ");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Couldn't parse line " + dbCommand);
            }
            return tokens[1].replace(";", "");
        }

        public boolean isNestedScript(String dbCommand) {
            return dbCommand.startsWith(HIVE_NESTING_TOKEN);
        }
    }

    // MySQL parser
    public static class MySqlCommandParser extends AbstractCommandParser {
        private static final String MYSQL_NESTING_TOKEN = "SOURCE";
        private static final String DELIMITER_TOKEN = "DELIMITER";
        private String delimiter = DEFAULT_DELIMITER;

        public MySqlCommandParser(String dbOpts, String msUsername, String msPassword,
                                  IronDBContext conf) {
            super(dbOpts, msUsername, msPassword, conf);
        }

        @Override
        public boolean isPartialCommand(String dbCommand) throws IllegalArgumentException {
            boolean isPartial = super.isPartialCommand(dbCommand);
            // if this is a delimiter directive, reset our delimiter
            if (dbCommand.startsWith(DELIMITER_TOKEN)) {
                String[] tokens = dbCommand.split(" ");
                if (tokens.length != 2) {
                    throw new IllegalArgumentException("Couldn't parse line " + dbCommand);
                }
                delimiter = tokens[1];
            }
            return isPartial;
        }

        public String getScriptName(String dbCommand) throws IllegalArgumentException {
            String[] tokens = dbCommand.split(" ");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Couldn't parse line " + dbCommand);
            }
            // remove ending ';'
            return tokens[1].replace(";", "");
        }

        public boolean isNestedScript(String dbCommand) {
            return dbCommand.startsWith(MYSQL_NESTING_TOKEN);
        }

        public String getDelimiter() {
            return delimiter;
        }

        @Override
        public String getQuoteCharacter() {
            return "`";
        }

        @Override
        public boolean isNonExecCommand(String dbCommand) {
            return super.isNonExecCommand(dbCommand) ||
                    (dbCommand.startsWith("/*") && dbCommand.endsWith("*/")) ||
                    dbCommand.startsWith(DELIMITER_TOKEN);
        }

        @Override
        public String cleanseCommand(String dbCommand) {
            return super.cleanseCommand(dbCommand).replaceAll("/\\*.*?\\*/[^;]", "");
        }

    }

    // Postgres specific parser
    public static class PostgresCommandParser extends AbstractCommandParser {
        private static final String POSTGRES_NESTING_TOKEN = "\\i";
        @VisibleForTesting
        public static final String POSTGRES_STANDARD_STRINGS_OPT = "SET standard_conforming_strings";
        @VisibleForTesting
        public static final String POSTGRES_SKIP_STANDARD_STRINGS_DBOPT = "postgres.filter.81";

        public PostgresCommandParser(String dbOpts, String msUsername, String msPassword,
                                     IronDBContext conf) {
            super(dbOpts, msUsername, msPassword, conf);
        }

        public String getScriptName(String dbCommand) throws IllegalArgumentException {
            String[] tokens = dbCommand.split(" ");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Couldn't parse line " + dbCommand);
            }
            // remove ending ';'
            return tokens[1].replace(";", "");
        }

        public boolean isNestedScript(String dbCommand) {
            return dbCommand.startsWith(POSTGRES_NESTING_TOKEN);
        }

        public boolean needsQuotedIdentifier() {
            return true;
        }

        public boolean isNonExecCommand(String dbCommand) {
            // Skip "standard_conforming_strings" command which is read-only in older
            // Postgres versions like 8.1
            // See: http://www.postgresql.org/docs/8.2/static/release-8-1.html
            if (getDbOpts().contains(POSTGRES_SKIP_STANDARD_STRINGS_DBOPT)) {
                if (dbCommand.startsWith(POSTGRES_STANDARD_STRINGS_OPT)) {
                    return true;
                }
            }
            return super.isNonExecCommand(dbCommand);
        }
    }

    //Oracle specific parser
    public static class OracleCommandParser extends AbstractCommandParser {
        private static final String ORACLE_NESTING_TOKEN = "@";

        public OracleCommandParser(String dbOpts, String msUsername, String msPassword,
                                   IronDBContext conf) {
            super(dbOpts, msUsername, msPassword, conf);
        }

        public String getScriptName(String dbCommand) throws IllegalArgumentException {
            if (!isNestedScript(dbCommand)) {
                throw new IllegalArgumentException("Not a nested script format " + dbCommand);
            }
            // remove ending ';' and starting '@'
            return dbCommand.replace(";", "").replace(ORACLE_NESTING_TOKEN, "");
        }

        public boolean isNestedScript(String dbCommand) {
            return dbCommand.startsWith(ORACLE_NESTING_TOKEN);
        }
    }

    //MSSQL specific parser
    public static class MSSQLCommandParser extends AbstractCommandParser {
        private static final String MSSQL_NESTING_TOKEN = ":r";

        public MSSQLCommandParser(String dbOpts, String msUsername, String msPassword,
                                  IronDBContext conf) {
            super(dbOpts, msUsername, msPassword, conf);
        }

        public String getScriptName(String dbCommand) throws IllegalArgumentException {
            String[] tokens = dbCommand.split(" ");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Couldn't parse line " + dbCommand);
            }
            return tokens[1];
        }

        public boolean isNestedScript(String dbCommand) {
            return dbCommand.startsWith(MSSQL_NESTING_TOKEN);
        }
    }

    public static NestedScriptParser getDbCommandParser(String dbName) {
        return getDbCommandParser(dbName, null);
    }

    public static NestedScriptParser getDbCommandParser(String dbName, String metaDbName) {
        return getDbCommandParser(dbName, null, null, null, null, metaDbName);
    }

    public static NestedScriptParser getDbCommandParser(String dbName,
                                                        String dbOpts, String msUsername, String msPassword,
                                                        IronDBContext conf, String metaDbType) {
        if (dbName.equalsIgnoreCase(DB_DERBY)) {
            return new DerbyCommandParser(dbOpts, msUsername, msPassword, conf);
        } else if (dbName.equalsIgnoreCase(DB_HIVE)) {
            return new HiveCommandParser(dbOpts, msUsername, msPassword, conf, metaDbType);
        } else if (dbName.equalsIgnoreCase(DB_MSSQL)) {
            return new MSSQLCommandParser(dbOpts, msUsername, msPassword, conf);
        } else if (dbName.equalsIgnoreCase(DB_MYSQL)) {
            return new MySqlCommandParser(dbOpts, msUsername, msPassword, conf);
        } else if (dbName.equalsIgnoreCase(DB_POSTGRACE)) {
            return new PostgresCommandParser(dbOpts, msUsername, msPassword, conf);
        } else if (dbName.equalsIgnoreCase(DB_ORACLE)) {
            return new OracleCommandParser(dbOpts, msUsername, msPassword, conf);
        } else {
            throw new IllegalArgumentException("Unknown dbType " + dbName);
        }
    }

    public static class MetaStoreConnectionInfo {
        private final String userName;
        private final String password;
        private final String url;
        private final String driver;
        private final boolean printInfo;
        private final IronDBContext conf;
        private final String dbType;
        private final int maxSize;
        private final int initSize;

        public String getUserName() {
            return userName;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public int getInitSize() {
            return initSize;
        }

        public MetaStoreConnectionInfo(
                boolean printInfo, IronDBContext ctx, String dbType) {
            super();
            maxSize = Integer.valueOf(ctx.get("IronDb.metastore.mysql.maxSize"));
            initSize = Integer.valueOf(ctx.get("IronDb.metastore.mysql.initSize"));
            this.userName = ctx.get("IronDb.metastore.mysql.username");
            this.password = ctx.get("IronDb.metastore.mysql.passwd");
            this.url = ctx.get("IronDb.metastore.mysql.uri");
            this.driver = ctx.get("IronDb.metastore.mysql.driver");
            this.printInfo = printInfo;
            this.conf = ctx;
            this.dbType = dbType;
        }

        public String getPassword() {
            return password;
        }

        public String getUrl() {
            return url;
        }

        public String getDriver() {
            return driver;
        }

        public boolean isPrintInfo() {
            return printInfo;
        }

        public IronDBContext getConf() {
            return conf;
        }

        public String getUsername() {
            return userName;
        }

        public boolean getPrintInfo() {
            return printInfo;
        }

        public String getDbType() {
            return dbType;
        }
    }
}
