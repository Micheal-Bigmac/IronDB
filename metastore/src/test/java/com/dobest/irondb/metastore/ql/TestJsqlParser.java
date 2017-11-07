package com.dobest.irondb.metastore.ql;

import com.alibaba.fastjson.JSON;
import com.dobest.irondb.metastore.util.SqlType;
import com.dobest.irondb.metastore.util.SqlUtil;
import com.google.common.collect.Sets;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestJsqlParser {

    @Test
    public void testCreate() throws JSQLParserException {

//        String sql =" create table a ( column1 saa,column2 varchar) with index1";
//        String sql =" desc a";
//        String sql ="load data local inpath wyp.txt into table wyp;";
//        String sql2 =" select a.* ,b.* from a,b where a.id=b.id";

//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("update ac_operator op ");
//        stringBuffer.append("set op.errcount=(");
//        stringBuffer.append("(select case when op1.errcount is null then 0 else op1.errcount end as errcount ");
//        stringBuffer.append("from ac_operator op1 ");
//        stringBuffer.append("where op1.loginname = '中国' )+1");
//        stringBuffer.append("),lastlogin='中国' ");
//        stringBuffer.append("where PROCESS_ID=");
//        stringBuffer.append("(select distinct g.id from tempTable g where g.ID='中国')");
//        stringBuffer.append("and columnName2 = '890' and columnName3 = '678' and columnName4 = '456'");

//        if(SqlUtil.checkSqlType(sql)== SqlType.LOAD){  // 处理JSQlParser 不支持的关键字 额外添加一些方法 替代修改JsqlParser 源码
//
//        }

        String select_1 = "select province,count(distinct user_id) from xxx group by province";
        String select_2 = " select a.* ,b.* from a,b where a.id=b.id";
        String select_3 = " select a.id ,b.name from a,b where a.id=b.id";
        String select_4 = " select a.id ,b.name from table1 a,table b where a.id=b.id";
        String select_5 = " select a.* ,b.* from a,b,c where a.id=b.id  and b.id=c.id ";
        String select_6 = " select id ,name from a,b,c where a.id=b.id  and b.id=c.id ";
        String select_7 = " select id ,name from a  where a.id in (select b.id from b)";
        Statement stmt = CCJSqlParserUtil.parse(select_1);
        Statement stmt2 = CCJSqlParserUtil.parse(select_2);
        Statement stmt3 = CCJSqlParserUtil.parse(select_3);
        Statement stmt4 = CCJSqlParserUtil.parse(select_4);
        Statement stmt5 = CCJSqlParserUtil.parse(select_5);
        Statement stmt6 = CCJSqlParserUtil.parse(select_6);
        Statement stmt7 = CCJSqlParserUtil.parse(select_7);
        boolean b1 = stmt instanceof Select;
        Select  sel=(Select) stmt;

        System.out.println(" "+ sel.toString());
        System.out.println("nihao ");
    }

    @Test
    public void testLoad1() {
        String sql1 = "load data local inpath wyp.txt into table wyp;";
        String sql2 = "load data local inpath wyp.txt OVERRIDE into table wyp";
        String sql3 = "load data inpath wyp.txt OVERRIDE into table wyp;";
//        String sql4 = "load data local inpath /user/bfas/export/wl_login_bm_his_sp/1509044938954/export.txt into table wl_login_bm_his_sp";
        String sql4 = "load data local inpath /user/bfas/export/yungengxin_user_mark_store/1509313175406/export.txt  into table yungengxin_user_mark";

      /*  Load load = SqlUtil.parseLoad(sql1);
        System.out.println(load.toString());
        Load load1 = SqlUtil.parseLoad(sql2);
        System.out.println(load1.toString());


        Load load2 = SqlUtil.parseLoad(sql3);
        System.out.println(load2.toString());*/

        Load load3 = SqlUtil.parseLoad(sql4);
        System.out.println(load3.toString());
    }

    @Test
    public void test4() {
        String sql3 = "create table test( uid string bitmap,nid string ,name string, province string,city string,day string) GROUP_SETS ('time,provice,city','time,provice')";

        try {
            Statement stmt = CCJSqlParserUtil.parse(sql3);
            boolean b = stmt instanceof CreateTable;
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test3() {
//        String blackEnd="local ";
        String blackEnd = " ";
        String replace = blackEnd.replaceAll("\\s$", "");
        System.out.println(replace.length());
    }

    @Test
    public void test5() {
//        String tmp="('time,provice,city','time,provice')";
//        String string=tmp.replaceAll("[',](.*)'+","$1");
//        System.out.println(string);
//   ('time,provice,city','time,provice')
//                      {"group_sets":"('time,provice,city','time,provice')"}
//          String beforeTrans={"group_sets":"('time,provice,city','time,provice')"};
//                     '{"group_sets":"('time,provice,city','time,provice')"}'
        String orignal = "{\"group_sets\":\"('time ,provice ,city'  ,  'time , provice','time,provice')\"}";
        Map parse = JSON.parseObject(orignal, Map.class);
        String group_sets = (String) parse.get("group_sets");
        group_sets = group_sets.replaceAll("\\s", "");
        System.out.println(group_sets);
        Pattern pattern = Pattern.compile("(?<=')[^']{2,}");
        Matcher matcher = pattern.matcher(group_sets);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
//        System.out.println(parse.size());

//        List<String> strings = Arrays.asList("11", "22", "33", "44");
        List<String> strings = null;
//        System.out.println(strings.size());
//        String tt=null;
//        if(!strings.contains(tt)){
//            System.out.println(strings);
//        }
    }


    @Test
    public void testListContaints() {
        List<List<String>> listlist = new ArrayList<>();
        List<String> list1 = Arrays.asList("1", "2", "3", "4", "5");
        List<String> list2 = Arrays.asList("4", "5");
        List<String> list3 = Arrays.asList("2", "3");
        List<String> list4 = Arrays.asList("3", "4", "5");
        listlist.add(list1);
        listlist.add(list2);
        listlist.add(list3);
        listlist.add(list4);
        if (listlist.contains(list2)) {
            System.out.println("true");
        }
        List<String> strings = null;
        "sfasdfasdfasdf".indexOf("sdf");
        if (listlist.contains(strings)) {
            System.out.println("true");
        } else {
            System.out.println(false);
        }
    }

    @Test
    public void testTimeCost() {
        Map<String, List<List<String>>> groupSets = new HashMap<>();                //  key 表名  value 对应他的group set 集合
        List<List<String>> group_One=new ArrayList<>();
        group_One.add(Arrays.asList("a","b","c","d"));
        group_One.add(Arrays.asList("b","c"));
        group_One.add(Arrays.asList("b","d"));
        groupSets.put("TableA",group_One);

        List<List<String>> group_Two=new ArrayList<>();
        group_Two.add(Arrays.asList("one","two","three","four"));
        group_Two.add(Arrays.asList("two","three"));
        group_Two.add(Arrays.asList("one","three"));
        groupSets.put("TableB",group_Two);

        List<List<String>> group_Three=new ArrayList<>();
        group_Three.add(Arrays.asList("a1","a2","a3","a4"));
        group_Three.add(Arrays.asList("a2","a3"));
        group_Three.add(Arrays.asList("a1","a3"));
        groupSets.put("TableC",group_Three);

        Map<String, List<String>> whereColumns = new HashMap<>();              //  where 条件后面的 表名对应的 列名 集合   要及时清除内存
        List<String> whereColumn_one=new ArrayList<>();
        whereColumn_one.add("b");
        whereColumn_one.add("d");
        whereColumns.put("TableA",whereColumn_one);

        List<String> whereColumn_Two=new ArrayList<>();
        whereColumn_Two.add("two");
        whereColumn_Two.add("three");
        whereColumns.put("TableB",whereColumn_Two);

        List<String> whereColumn_Three=new ArrayList<>();
        whereColumn_Three.add("a1");
        whereColumn_Three.add("a4");
        whereColumns.put("TableC",whereColumn_Three);



        Map<String, Set<String>> tableGroupcolumns = new HashMap<>();      // key 表名 value  对应的group set  字段集合
        for(String tableName : groupSets.keySet()){
            List<List<String>> lists = groupSets.get(tableName);
            Set<String> sets=new HashSet<>();
            for(List<String> tmp: lists){
                sets.addAll(tmp);
            }
            tableGroupcolumns.put(tableName,sets);
        }

        // (group_Sets (a,b,c) where a b c 必须有确定的字段)    // 时间复杂度有点高   待改成字符串匹配Kmp算法
        for (String tableName : groupSets.keySet()) {
            Set<String> tableGroupset = tableGroupcolumns.get(tableName);   // 表对应的group_set 列有哪些
            List<String> whereColumn = whereColumns.get(tableName);          // 判断 where 条件字段是否在group_set set集合中
            List<String> whereInGroupSet = new ArrayList<>();
            for (String isWhereInGroupSet : whereColumn) {
                if (tableGroupset.contains(isWhereInGroupSet)) {
                    whereInGroupSet.add(isWhereInGroupSet);
                }
            }
            List<List<String>> groupSetsColList = groupSets.get(tableName);  //  group_Sets  中的多个分组
            boolean flag = false;
            for (int i = 0; i < groupSetsColList.size(); i++) {
                List<String> echoGroupSet = groupSetsColList.get(i);
                if (echoGroupSet.size() == whereInGroupSet.size()) {  // 每组group set 字段数目 和 where 出现在group_set 字段数一样
                    for (int j = 0; j < whereInGroupSet.size(); j++) {
                        String tmp = whereInGroupSet.get(j);
                        if (!echoGroupSet.contains(tmp)) {
                            break;
                        }
                        if (j == whereInGroupSet.size() - 1) {
                            i = groupSetsColList.size() - 1;
                            flag = true;
                        }
                    }
                }
            }
            if (flag == false) {
                throw new IllegalArgumentException(" table "+tableName+"(group_Sets (" + whereColumn + ") where 表达式 必须要 有" + whereColumn + " 字段) ");
            }
        }

    }

    @Test
    public void test7(){
        String tt="W_LOGIN$A";
        System.out.println(tt.replaceAll("(.*)\\$.*","$1"));
    }

    @Test
    public  void test8(){
        List<String> tables =new ArrayList<>();
        String t1="from w_login a   inner join w_registeron b  on a.user_id = b.user_id left join mytable d on d.id=c.id where ";
//        String t1="SELECT count(DISTINCT a.bmid_user) FROM  wl_login_bm_his_sp a   where a.data_day BETWEEN '2017-07-24' AND '2017-07-24' AND a.app_id = '205_205'";
        System.out.println(t1);
        t1=t1.toLowerCase().replaceAll("(\\sinner|\\sleft|\\sright|\\swhere|\\son)", "%");
        System.out.println(t1);
//        Pattern pattern=Pattern.compile("(?<=from|join)[^on]+");
        Pattern pattern = Pattern.compile("(?<=from |join )[^%]+");
//        Pattern pattern=Pattern.compile(".*from  (.*)inner join (.*)");
        Matcher matcher = pattern.matcher(t1);
        while(matcher.find()){
            String group = matcher.group();
            String result= group.split("\\s")[0];
            tables.add(result);
            System.out.println("orignal :"+group +" now : "+result);

        }
    }


    public  void test9(){
        String orignal = "{\"group_sets\":\"('time ,provice ,city'  ,  'time , provice','time,provice')\"}";
        Map parse = JSON.parseObject(orignal, Map.class);
        String group_sets = (String) parse.get("group_sets");
        group_sets = group_sets.replaceAll("\\s", "");
        System.out.println(group_sets);
        Pattern pattern = Pattern.compile("(?<=')[^']{2,}");
        Matcher matcher = pattern.matcher(group_sets);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }


    @Test
    public  void test10(){
        String str = "程序设计协会";
        // 正则表达式规则
        String strRegex = "程序.*";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(strRegex);
        //创建匹配器
        Matcher matcher = pattern.matcher(str);
        // 查找字符串中是否有匹配正则表达式的 字符/字符串
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }


    @Test
    public  void test11(){
        String tt="(uid,name,sdfasd,  sdfsad ,sfas)";
        Pattern compile = Pattern.compile("(?<=\\(|,)[^)|,]+");
        Matcher matcher = compile.matcher(tt.replaceAll("\\s",""));
        while (matcher.find()){
            System.out.println(matcher.group());
        }
    }



}
