package com.dobest.irondb.metastore.ql;

import com.alibaba.fastjson.JSON;
import com.dobest.irondb.metastore.bean.TableInfo;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSqlDefinitor {

    @Test
    public void testDML1() throws JSQLParserException {
        String sql1=" create table w_register(\n" +
                "        uid string   bitmap,\n" +
                "        nid string   bitmap,\n" +
                "        name string,\n" +
                "        province string,\n" +
                "        city string,\n" +
                "        day string\n" +
                " )\n" +
                "    GROUP_SETS=day_province_city day_province ";
// ttl=4000
        String sql2="create table user_image(uid LONG,name INT  bitmap,birthday Date,first_logintime long,province array auto_dimension,love_game map auto_dimension) PRIMARY_KEY(uid,name) TTL 86400;";
        String sql3="    create table test( uid string ,nid string ,name string, province string,city string,day string) GROUP_SETS (time,provice,city,time,provice)\n TTL 86400";
        String sql4="DROP TABLE IF EXISTS table_name;";
        String sql5="TRUNCATE TABLE table_name;";

      /*  String sql6 ="ALTER TABLE bouns rename table bonus_new";  // error    alter 中 可以操作的类型 只有 add drop modify  没有 rename
        String sql6_1="ALTER TABLE mytable ADD COLUMN mycolumn varchar (255)";
        String sql6_2="ALTER TABLE table DROP COLUMN field";

        String sql7="ALTER TABLE t1 modify a b int comment song";    ///  error   change 不支持 仅仅支持 modify  源码中有但是未实现change 功能
        String sql8="ALTER TABLE table_name ADD INDEX1  song>";   // error alter 操作 后面只能支持 modify add drop 操作
        String sql8_1="ALTER TABLE table_name DROP INDEX 22";   //errir*/

        String sql9_1="CREATE VIEW view_name (column_name ,name ,age) AS SELECT a, b, c, COUNT(DISTINCT d) FROM table_name GROUP BY a, b, c";
        Statement stmt = CCJSqlParserUtil.parse(sql3);

        boolean b = stmt instanceof CreateTable;
        System.out.println(b);
    }
    @Test
    public void sqlTrans(){
        String errorSql=")GROUP SETS((day,province,city)),(day,province)";
        System.out.println(errorSql.toLowerCase());
        errorSql=errorSql.toLowerCase().replaceAll("(.*)\\),\\(.*","$1");
//        errorSql.replaceAll("",)
//        Pattern p = Pattern.compile("");
        System.out.println(errorSql);
    }
    @Test
    public void test3(){
        String value = "([1],4), ([11, 3],3), ([1, 2],3)";
        Pattern pattern = Pattern.compile("(?<=\\[[^\\\\]\\\\])\\d+");
        Matcher matcher = pattern.matcher(value);
        while(matcher.find()){
            System.out.println(matcher.group(1));
        }
    }


    @Test
    public  void test4(){
        String sql="{\"storage_type\":\"detail\",\"table_options\":\"{\\\"primary_key\\\":\\\"(uid,name)\\\",\\\"ttl\\\":\\\"86400\\\"}\",\"id\":\"1507705015441000\",\"tablename\":\"user_image8\",\"status\":\"1\"}";
        TableInfo transFormResult = JSON.parseObject(sql,TableInfo.class);

        System.out.println(transFormResult.getTable_options());
    }
}
