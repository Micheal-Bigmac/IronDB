package com.irondb.metastore.mysql;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.ValidateCheck;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MetaStoreTool {
    private final String IronDb_Home;
    private String userName = null;
    private String passWord = null;
    private boolean dryRun = false;
    private boolean verbose = false;
    private String dbOpts = null;
    private String url = null;
    private String driver = null;
    private final com.irondb.metastore.IronDBContext IronDBContext;
    private final String dbType;
    private final String metaDbType;
//    private final IIronDBContext IronDBContext;
    private boolean needsQuotedIdentifier;

    static final private Logger LOG = LoggerFactory.getLogger(MetaStoreTool.class.getName());

    // 需要配置 IronDB_HOME  用来寻找 mysql初始化metadata sql 文件
    public MetaStoreTool(String dbType, String metaDbType, InputStream inputStream) throws IronDBMetaException {
//        this(System.getenv("HIVE_HOME"), new IronDBContext(), dbType, metaDbType);
        this("E:\\github\\IronDB\\metastore\\src\\main\\resources", com.irondb.metastore.IronDBContext.fromInputStream(MetaStoreTool.class.getResourceAsStream("/IronDB.properties")), dbType, metaDbType);
    }

    public MetaStoreTool(String IronDBHome, IronDBContext IronDBContext, String dbType, String metaDbType)
            throws IronDBMetaException {
        if (ValidateCheck.isEmpty(IronDBHome)) {
            throw new IronDBMetaException("No Hive home directory provided");
        }
        this.IronDb_Home=IronDBHome;
        this.IronDBContext = IronDBContext;
        this.dbType = dbType;
        this.metaDbType = metaDbType;
        this.needsQuotedIdentifier = getDbCommandParser(dbType).needsQuotedIdentifier();
    }

    public IronDBContext getIronDBContext() {
        return IronDBContext;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setDbOpts(String dbOpts) {
        this.dbOpts = dbOpts;
    }

    private static void printAndExit(Options cmdLineOptions) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("schemaTool", cmdLineOptions);
        System.exit(1);
    }

    Connection getConnectionToMetastore(boolean printInfo)
            throws IronDBMetaException {
        return IronDbSchemeFactory.getConnectionToMetastore(userName,
                passWord, url, driver, printInfo);
    }

    private IronDbSchemeFactory.NestedScriptParser getDbCommandParser(String dbType, String metaDbType) {
        return IronDbSchemeFactory.getDbCommandParser(dbType, dbOpts, userName,
                passWord, IronDBContext, metaDbType);
    }

    private IronDbSchemeFactory.NestedScriptParser getDbCommandParser(String dbType) {
        return IronDbSchemeFactory.getDbCommandParser(dbType, dbOpts, userName,
                passWord, IronDBContext, null);
    }

    // test the connection metastore using the config property
    private void testConnectionToMetastore() throws IronDBMetaException {
        Connection conn = getConnectionToMetastore(true);
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IronDBMetaException("Failed to close metastore connection", e);
        }
    }
    /**
     * Initialize the metastore schema
     *
     * @param
     * @throws MetaException
     */

    // initScriptFile sql 脚本
    public void doInit() throws IronDBMetaException {
        testConnectionToMetastore();

        String initScriptFile = IronDBContext.getMetaStoreScript();

        try {
            System.out.println("Initialization script " + initScriptFile);
            if (!dryRun) {
                runBeeLine(this.IronDb_Home, initScriptFile);
                System.out.println("Initialization script completed");
            }
        } catch (IOException e) {
            throw new IronDBMetaException("Schema initialization FAILED!" +
                    " Metastore state would be inconsistent !!", e);
        }
    }

    public void doValidate() throws IronDBMetaException {
        System.out.println("Starting metastore validation\n");
        Connection conn = getConnectionToMetastore(false);
        boolean success = true;
        try {

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new IronDBMetaException("Failed to close metastore connection", e);
                }
            }
        }
        System.out.print("Done with metastore validation: ");
        if (!success) {
            System.out.println("[FAIL]");
            System.exit(1);
        } else {
            System.out.println("[SUCCESS]");
        }
    }

    /***
     * Run beeline with the given metastore script. Flatten the nested scripts
     * into single file.
     */
    private void runBeeLine(String scriptDir, String scriptFile)
            throws IOException, IronDBMetaException {
        IronDbSchemeFactory.NestedScriptParser dbCommandParser = getDbCommandParser(dbType, metaDbType);

        String sqlCommands = dbCommandParser.buildCommand(scriptDir, scriptFile, metaDbType != null);
        Connection conn = getConnectionToMetastore(false);
        Statement statement=null;
        try {
            statement = conn.createStatement();
            statement.execute(sqlCommands);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }
    // 真正运行  sql scrip

    // Create the required command line options
    @SuppressWarnings("static-access")
    private static void initOptions(Options cmdLineOptions) {
        Option help = new Option("help", "print this message");
        Option upgradeOpt = new Option("upgradeSchema", "Schema upgrade");
        Option upgradeFromOpt = OptionBuilder.withArgName("upgradeFrom").hasArg().
                withDescription("Schema upgrade from a version").
                create("upgradeSchemaFrom");
        Option initOpt = new Option("initSchema", "Schema initialization");
        Option initToOpt = OptionBuilder.withArgName("initTo").hasArg().
                withDescription("Schema initialization to a version").
                create("initSchemaTo");
        Option infoOpt = new Option("info", "Show config and schema details");
        Option validateOpt = new Option("validate", "Validate the database");

        OptionGroup optGroup = new OptionGroup();
        optGroup.addOption(upgradeOpt).addOption(initOpt).
                addOption(help).addOption(upgradeFromOpt).
                addOption(initToOpt).addOption(infoOpt).addOption(validateOpt);
        optGroup.setRequired(true);

        Option userNameOpt = OptionBuilder.withArgName("user")
                .hasArgs()
                .withDescription("Override config file user name")
                .create("userName");
        Option passwdOpt = OptionBuilder.withArgName("password")
                .hasArgs()
                .withDescription("Override config file password")
                .create("passWord");
        Option dbTypeOpt = OptionBuilder.withArgName("databaseType")
                .hasArgs().withDescription("Metastore database type")
                .create("dbType");
        Option metaDbTypeOpt = OptionBuilder.withArgName("metaDatabaseType")
                .hasArgs().withDescription("Used only if upgrading the system catalog for hive")
                .create("metaDbType");
        Option urlOpt = OptionBuilder.withArgName("url")
                .hasArgs().withDescription("connection url to the database")
                .create("url");
        Option driverOpt = OptionBuilder.withArgName("driver")
                .hasArgs().withDescription("driver name for connection")
                .create("driver");
        Option dbOpts = OptionBuilder.withArgName("databaseOpts")
                .hasArgs().withDescription("Backend DB specific options")
                .create("dbOpts");
        Option dryRunOpt = new Option("dryRun", "list SQL scripts (no execute)");
        Option verboseOpt = new Option("verbose", "only print SQL statements");
        Option serversOpt = OptionBuilder.withArgName("serverList")
                .hasArgs().withDescription("a comma-separated list of servers used in location validation in the format of scheme://authority (e.g. hdfs://localhost:8000)")
                .create("servers");
        cmdLineOptions.addOption(help);
        cmdLineOptions.addOption(dryRunOpt);
        cmdLineOptions.addOption(userNameOpt);
        cmdLineOptions.addOption(passwdOpt);
        cmdLineOptions.addOption(dbTypeOpt);
        cmdLineOptions.addOption(verboseOpt);
        cmdLineOptions.addOption(metaDbTypeOpt);
        cmdLineOptions.addOption(urlOpt);
        cmdLineOptions.addOption(driverOpt);
        cmdLineOptions.addOption(dbOpts);
        cmdLineOptions.addOption(serversOpt);
        cmdLineOptions.addOptionGroup(optGroup);
    }

    public static void main(String[] args) {
        CommandLineParser parser = new GnuParser();
        CommandLine line = null;
        String dbType = null;
        String metaDbType = null;
        Options cmdLineOptions = new Options();

        // Argument handling
        initOptions(cmdLineOptions);
        try {
            line = parser.parse(cmdLineOptions, args);
        } catch (ParseException e) {
            System.err.println("MetaStoreTool:Parsing failed.  Reason: " + e.getLocalizedMessage());
            printAndExit(cmdLineOptions);
        }

        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("schemaTool", cmdLineOptions);
            return;
        }

        if (line.hasOption("dbType")) {
            dbType = line.getOptionValue("dbType");
            if ((!dbType.equalsIgnoreCase(IronDbSchemeFactory.DB_DERBY) &&
                    !dbType.equalsIgnoreCase(IronDbSchemeFactory.DB_HIVE) &&
                    !dbType.equalsIgnoreCase(IronDbSchemeFactory.DB_MSSQL) &&
                    !dbType.equalsIgnoreCase(IronDbSchemeFactory.DB_MYSQL) &&
                    !dbType.equalsIgnoreCase(IronDbSchemeFactory.DB_POSTGRACE) && !dbType
                    .equalsIgnoreCase(IronDbSchemeFactory.DB_ORACLE))) {
                System.err.println("Unsupported dbType " + dbType);
                printAndExit(cmdLineOptions);
            }
        } else {
            System.err.println("no dbType supplied");
            printAndExit(cmdLineOptions);
        }

        if (line.hasOption("metaDbType")) {
            metaDbType = line.getOptionValue("metaDbType");

            if (!dbType.equals(IronDbSchemeFactory.DB_HIVE)) {
                System.err.println("metaDbType only supported for dbType = hive");
                printAndExit(cmdLineOptions);
            }

            if (!metaDbType.equalsIgnoreCase(IronDbSchemeFactory.DB_DERBY) &&
                    !metaDbType.equalsIgnoreCase(IronDbSchemeFactory.DB_MSSQL) &&
                    !metaDbType.equalsIgnoreCase(IronDbSchemeFactory.DB_MYSQL) &&
                    !metaDbType.equalsIgnoreCase(IronDbSchemeFactory.DB_POSTGRACE) &&
                    !metaDbType.equalsIgnoreCase(IronDbSchemeFactory.DB_ORACLE)) {
                System.err.println("Unsupported metaDbType " + metaDbType);
                printAndExit(cmdLineOptions);
            }
        } else if (dbType.equalsIgnoreCase(IronDbSchemeFactory.DB_HIVE)) {
            System.err.println("no metaDbType supplied");
            printAndExit(cmdLineOptions);
        }

        try {
//            InputStream inputStream = Resources.getResource("IronDB.properties").openStream();
            InputStream inputStream =MetaStoreTool.class.getResource("/IronDB.proerties").openStream();
            MetaStoreTool schemaTool = new MetaStoreTool(dbType, metaDbType,inputStream);

            if (line.hasOption("userName")) {
                schemaTool.setUserName(line.getOptionValue("userName"));
            } else {
                throw new IronDBMetaException("Error getting metastore userName");
            }
            if (line.hasOption("passWord")) {
                schemaTool.setPassWord(line.getOptionValue("passWord"));
            } else {
                    throw new IronDBMetaException("Error getting metastore password");
            }
            if (line.hasOption("url")) {
                schemaTool.setUrl(line.getOptionValue("url"));
            }
            if (line.hasOption("driver")) {
                schemaTool.setDriver(line.getOptionValue("driver"));
            }
            if (line.hasOption("dryRun")) {
                schemaTool.setDryRun(true);
            }
            if (line.hasOption("verbose")) {
                schemaTool.setVerbose(true);
            }
            if (line.hasOption("dbOpts")) {
                schemaTool.setDbOpts(line.getOptionValue("dbOpts"));
            }
            if (line.hasOption("validate") && line.hasOption("servers")) {
//                schemaTool.setValidationServers(line.getOptionValue("servers"));
            }
            if (line.hasOption("initSchema")) {
                schemaTool.doInit();
            } else if (line.hasOption("initSchemaTo")) {
                schemaTool.doInit();
            } else if (line.hasOption("validate")) {
                schemaTool.doValidate();
            } else {
                System.err.println("no valid option supplied");
                printAndExit(cmdLineOptions);
            }
        } catch (IronDBMetaException e) {
            System.err.println(e);
            if (e.getCause() != null) {
                Throwable t = e.getCause();
                System.err.println("Underlying cause: "
                        + t.getClass().getName() + " : "
                        + t.getMessage());
                if (e.getCause() instanceof SQLException) {
                    System.err.println("SQL Error code: " + ((SQLException)t).getErrorCode());
                }
            }
            if (line.hasOption("verbose")) {
                e.printStackTrace();
            } else {
                System.err.println("Use --verbose for detailed stacktrace.");
            }
            System.err.println("*** schemaTool failed ***");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("schemaTool completed");

    }
}
