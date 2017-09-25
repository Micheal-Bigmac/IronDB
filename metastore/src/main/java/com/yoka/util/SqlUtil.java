package com.yoka.util;

import java.sql.*;

/**
 * Created by Micheal on 2017/9/23.
 */
public class SqlUtil {
    public void getSqlType(ResultSet rs, PreparedStatement mstmt) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
     /*   switch (type) {
            case Types.BIGINT:
                mstmt.setLong(i, rs.getLong(name));
                break;
            case Types.BOOLEAN:
                mstmt.setBoolean(i, rs.getBoolean(name));
                break;
            case Types.DATE:
                mstmt.setDate(i, rs.getDate(name));
                break;
            case Types.DOUBLE:
                mstmt.setDouble(i, rs.getDouble(name));
                break;
            case Types.FLOAT:
                mstmt.setFloat(i, rs.getFloat(name));
                break;
            case Types.INTEGER:
                mstmt.setInt(i, rs.getInt(name));
                break;
            case Types.SMALLINT:
                mstmt.setInt(i, rs.getInt(name));
                break;
            case Types.TIME:
                mstmt.setTime(i, rs.getTime(name));
                break;
            case Types.TIMESTAMP:
                mstmt.setTimestamp(i, rs.getTimestamp(name));
                break;
            case Types.TINYINT:
                mstmt.setShort(i, rs.getShort(name));
                break;
            case Types.VARCHAR:
                mstmt.setString(i, rs.getString(name));
                break;
            case Types.NCHAR:
                mstmt.setString(i, rs.getNString(name));
                break;
            case Types.NVARCHAR:
                mstmt.setString(i, rs.getNString(name));
                break;
            case Types.BIT:
                mstmt.setByte(i, rs.getByte(name));
                break;
        }*/
    }
    public SqlType checkSqlType(String sql){
        String tmp=sql.trim().toLowerCase();
        if(sql.startsWith("create")) {
            return SqlType.valueOf(0);
        } else if (sql.startsWith("select")) {
            return SqlType.valueOf(1);
        }else if(sql.startsWith("insert")){
            return SqlType.valueOf(2);
        }else if(sql.startsWith("update")){
            return SqlType.valueOf(3);
        }else if(sql.startsWith("delete")){
            return SqlType.valueOf(4);
        }else if(sql.startsWith("alter")){
            return SqlType.valueOf(5);
        }else if(sql.startsWith("desc")){
            return SqlType.valueOf(6);
        }else {
            return SqlType.valueOf(7);
        }
    }
}
