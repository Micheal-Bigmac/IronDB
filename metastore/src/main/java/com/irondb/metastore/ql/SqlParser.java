package com.irondb.metastore.ql;

import com.irondb.metastore.bean.TableInfo;
import com.irondb.metastore.bean.TableSchema;
import com.irondb.metastore.util.GenericID;
import com.sun.deploy.util.StringUtils;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlParser {
    private static final String IronDBTablesName = "IronDBTables";
    private static final String IronDBColumnsMames = "IronDBColumns";
    private static final String addTableSqls = "insert into IronDBTables (id,tablename,status,storage_Type) values(";
    private static final String addTableColumnSqls = "insert into IronDBColumns (column_name,type,irondb_id,suppor_function) values (";



    public static List<String> parseCreatTable(Statement parse) {
        TableInfo tableInfo = new TableInfo();
        List<TableSchema> tableSchemas = new ArrayList<>();

        StringBuffer buffer = new StringBuffer(addTableSqls);
        Table table = ((CreateTable) parse).getTable();
        String tableName = table.getName();

        tableInfo.setTablename(tableName);
        tableInfo.setStatus(true);
        long primaryKey = GenericID.nextId();
        tableInfo.setId(primaryKey);

        List<String> tableOptionsStrings = (List<String>) ((CreateTable) parse).getTableOptionsStrings();
        Map<String, Object> stringObjectMap = paraseTableOptions(tableOptionsStrings);

        if (stringObjectMap.containsKey("primary_key")) {
            tableInfo.setStorage_type("detail");
        } else if (stringObjectMap.containsKey("group_sets")) {
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
            buffer.append(",'").append(ob.getStorage_type()).append("')");
        }else{
            buffer.append(",'").append("").append("')");
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
}
