package com.yoka.ql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

public class TestJsqlParser {

    @Test
    public void testCreate() throws JSQLParserException {

//        String sql =" create table a ( column1 saa,column2 varchar) with index1";
        String sql =" desc a";
        String sql2 =" select a.* ,b.* from a,b where a.id=b.id";

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("update ac_operator op ");
        stringBuffer.append("set op.errcount=(");
        stringBuffer.append("(select case when op1.errcount is null then 0 else op1.errcount end as errcount ");
        stringBuffer.append("from ac_operator op1 ");
        stringBuffer.append("where op1.loginname = '中国' )+1");
        stringBuffer.append("),lastlogin='中国' ");
        stringBuffer.append("where PROCESS_ID=");
        stringBuffer.append("(select distinct g.id from tempTable g where g.ID='中国')");
        stringBuffer.append("and columnName2 = '890' and columnName3 = '678' and columnName4 = '456'");

        Statement stmt = CCJSqlParserUtil.parse(sql);
        Statement stmt2 = CCJSqlParserUtil.parse(sql2);
        boolean b = stmt instanceof CreateTable;
        boolean b1 = stmt2 instanceof Select;
        System.out.println("nihao ");
    }


}
