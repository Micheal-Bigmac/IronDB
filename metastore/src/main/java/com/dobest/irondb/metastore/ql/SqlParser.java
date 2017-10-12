package com.dobest.irondb.metastore.ql;

import com.alibaba.fastjson.JSON;
import com.dobest.irondb.metastore.bean.TableInfo;
import com.dobest.irondb.metastore.bean.TableSchema;
import com.dobest.irondb.metastore.mysql.IronDBMetaDataCache;
import com.dobest.irondb.metastore.util.GenericID;
import com.sun.deploy.util.StringUtils;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.drop.Drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlParser {
    private static final String IronDBTablesName = "IronDBTables";
    private static final String IronDBColumnsMames = "IronDBColumns";
    private static final String addTableSqls = "insert into IronDBTables (id,tablename,status,storage_Type,table_options) values(";
    private static final String addTableColumnSqls = "insert into IronDBColumns (column_name,type,irondb_id,suppor_function) values (";

    private static final String dropTableSqls="delete from IronDBTables where id=";
    private static final String dropTableColumnSqls="delete from irondbcolumns where irondbcolumns.irondb_id =";

    /***
     * 暂时未处理 tableOptions 参数
     * @param parse
     * @param dbCache
     * @return
     */
    public static List<String> parseCreatTableForMysql(Statement parse,IronDBMetaDataCache dbCache) {
        TableInfo tableInfo = new TableInfo();
        List<TableSchema> tableSchemas = new ArrayList<>();

        StringBuffer buffer = new StringBuffer(addTableSqls);
        Table table = ((CreateTable) parse).getTable();
        String tableName = table.getName();

        tableInfo.setTablename(tableName);
        tableInfo.setStatus(true);
        long primaryKey = GenericID.nextId();
//        long primaryKey = 2;
        tableInfo.setId(primaryKey);
        // 往内存中添加表相关缓存数据


        Map<String, TableInfo> tables = dbCache.getTables();
        if(!tables.containsKey(tableName)){
            tables.put(tableName,tableInfo);
            dbCache.getTable_metaData().put(tableName,tableSchemas);
        }else {
            return null;
        }


        List<String> tableOptionsStrings = (List<String>) ((CreateTable) parse).getTableOptionsStrings();
        Map<String, Object> tableOptionsMap = paraseTableOptions(tableOptionsStrings);

        tableInfo.setTableOptionsMap(tableOptionsMap);

        if (tableOptionsMap.containsKey("primary_key")) {
            tableInfo.setStorage_type("detail");
        } else if (tableOptionsMap.containsKey("group_sets")) {
            tableInfo.setStorage_type("preagg");
        }

        TableSchema schema = null;
        List<ColumnDefinition> columnDefinitions = ((CreateTable) parse).getColumnDefinitions();
        for (ColumnDefinition definition : columnDefinitions) {
            schema = new TableSchema();
            schema.setColumn_name(definition.getColumnName());
            schema.setType(definition.getColDataType().getDataType());
            List<String> columnSpecStrings = definition.getColumnSpecStrings();
            if (columnSpecStrings != null) {
                String support = StringUtils.join(columnSpecStrings, "|");
                schema.setSuppor_function(support);
            }
            schema.setIrondb_id(primaryKey);
            tableSchemas.add(schema);
        }
        List<String> strings = beanToSql(tableInfo, tableSchemas);
        return strings;
    }

    private static Map<String, Object> paraseTableOptions(List<String> options) {
        int size = options.size();
        Map<String, Object> opts = new HashMap<>();
        if (size % 2 == 0) {
            for (int i = 0; i < size; i++) {
                String s = options.get(i).toLowerCase();
                i += 1;
                opts.put(s, options.get(i));
            }
        }
        return opts;
    }


    private static List<String> beanToSql(TableInfo ob, List<TableSchema> schemas) {
//        (id,tablename,status,storage_Type)
        List<String> sqls = new ArrayList<>();
        StringBuffer buffer = null;
        buffer = new StringBuffer();
        buffer.append(addTableSqls).append(ob.getId()).append(",'").append(ob.getTablename()).append("',").append(1);
        if(ob.getStorage_type()!=null){
            buffer.append(",'").append(ob.getStorage_type()).append("',");
        }else{
            buffer.append(",'").append("").append("',");
        }

        if(ob.getTableOptionsMap().size()>0){
            buffer.append("'").append(ob.getTable_options()).append("')");
        }else{
            buffer.append("'").append("").append("')");
        }

        sqls.add(buffer.toString());

//        (column_name,type,irondb_id,comments,suppor_function)
        for (TableSchema schema : schemas) {
            buffer = new StringBuffer();
            buffer.append(addTableColumnSqls).append("'").append(schema.getColumn_name()).append("','").append(schema.getType()).append("',").append(schema.getIrondb_id());
            if(schema.getSuppor_function()!=null){
                buffer .append(",'").append(schema.getSuppor_function()).append("')");
            }else {
                buffer.append(",'").append("").append("')");
            }


            sqls.add(buffer.toString());
        }
        return sqls;
    }

    public static List<String> parseDropTableForMysql(Statement parse, IronDBMetaDataCache metaDataCache) {
        Drop drop = (Drop) parse;
        String tableName = drop.getName().getName();
        TableInfo tableInfo = metaDataCache.getTables().get(tableName);
        long mysql_key=tableInfo.getId();
        List<String> sqls=new ArrayList<>();
        StringBuffer buffer=new StringBuffer();
        buffer.append(dropTableSqls).append(mysql_key);
        sqls.add(buffer.toString());
        buffer=new StringBuffer();
        buffer.append(dropTableColumnSqls).append(mysql_key);
        sqls.add(buffer.toString());
        return sqls;
    }
}
