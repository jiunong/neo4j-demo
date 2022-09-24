package com.cxm.neo4j;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.cxm.neo4j.database.DecoratorDbQuery;
import com.cxm.neo4j.database.Neo4jJdbc;
import com.cxm.neo4j.database.Neo4jQuery;
import com.cxm.neo4j.model.Person;
import com.cxm.neo4j.util.GeneratePersonInfoCsv;
import com.cxm.neo4j.util.Neo4jUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@SpringBootTest
class Neo4jDemoApplicationTests {

    @Test
    void contextLoads() {
        List<Person> persons = GeneratePersonInfoCsv.getPersons(100);
        EasyExcel.write("F:\\demo.csv", Person.class).sheet("模板").doWrite(persons);
    }

    @Test
    void testQueryNeo4j() throws Exception {
        Connection connection = Neo4jJdbc.getConnection();
        DecoratorDbQuery dbQuery = new DecoratorDbQuery(connection);
        try (PreparedStatement preparedStatement = connection.prepareStatement("sql")) {
            dbQuery.query(preparedStatement, u1 -> {
                System.out.println(u1.getResultSet().toString());
            });
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Test
    void write() {
        List<Person> persons = GeneratePersonInfoCsv.getPersons(200);
        persons.forEach(person -> {
            StringBuilder cypher = new StringBuilder();
            cypher.append("CREATE (n:person{姓名:$name,性别:$sex,姓:$familyName,身份证:$idCard})");
            Neo4jQuery.write(String.valueOf(cypher), u -> {

            }, "name", person.getName(), "sex", person.getSex(), "familyName", person.getFamilyName(), "idCard", person.getIdCard());
        });

    }

    @Test
    void createShip() {
        Neo4jQuery.write("MATCH (n:person),(m:person) where n.身份证 <> m.身份证 and n.姓 = m.姓 CREATE (n)-[r:同姓氏]->(m) RETURN r", u -> {
        });
    }

    @Test
    void createLabel() {
        Neo4jQuery.write("MATCH (n:person) WHERE n.性别 = $sex SET n:male ", u -> {
        }, "sex", "男");
    }

    @Test
    void delLabl() {
        Neo4jQuery.write("MATCH (n) remove n:female", u -> {
        });
    }

    @Test
    void newShip() {
        Queue female = new LinkedList();
        Queue male = new LinkedList();
        Neo4jQuery.query("MATCH (n:female) WHERE $beginYear < toInteger(substring(n.身份证,6,4)) < $endYear   RETURN n.身份证 as femaleId", u -> {
            while (u.getResultSet().hasNext()) {
                String femaleId = u.getResultSet().next().get("femaleId").asString();
                if (!female.contains(femaleId)) {
                    female.add(femaleId);
                }

            }
        }, "beginYear", 1982, "endYear", 1992);
        Neo4jQuery.query("MATCH (m:male)  WHERE   $beginYear < toInteger(substring(m.身份证,6,4)) < $endYear  RETURN m.身份证 as maleId", u -> {
            while (u.getResultSet().hasNext()) {
                String maleId = u.getResultSet().next().get("maleId").asString();
                if (!male.contains(maleId)) {
                    male.add(maleId);
                }

            }
        }, "beginYear", 1982, "endYear", 1992);
        while (!female.isEmpty() && !male.isEmpty()) {
            Neo4jQuery.write("MATCH (n:person{身份证:'"+female.poll()+"'}), (m:person{身份证:'"+male.poll()+"'})  create (n)-[r:夫妻]->(m),(m)-[r1:夫妻]->(n) ",
                    neo4jResultWrapper -> {
                neo4jResultWrapper.getResultSet();
            });
        }
    }

    @Test
    void loadJson(){
        String records = FileUtil.readUtf8String("C:\\Users\\Administrator\\Desktop\\records.json");
        JSONArray values = JSONUtil.parseArray(records);
        values.stream().map(JSONObject::new).forEach(u->{
            JSONObject n = Neo4jUtil.ObjectToJsonList(u.get("n")).get(0);
            List<JSONObject> labels = Neo4jUtil.ObjectToJsonList(n.get("labels"));
            List<JSONObject> properties = Neo4jUtil.ObjectToJsonList(n.get("properties"));
            JSONObject entries = properties.get(0);
            Iterator<Map.Entry<String, Object>> iterator = entries.entrySet().iterator();
            String sql = "CREATE (n:飞机出行{承运航空公司代码:'"+entries.get("承运航空公司代码")+"'" +
                    ",姓名:'"+entries.get("姓名")+"'" +
                    ",时间:'"+entries.get("时间")+"'" +
                    ",登机机场:'"+entries.get("登机机场")+"'" +
                    ",地点:'"+entries.get("地点")+"'" +
                    ",航班号:'"+entries.get("航班号")+"'" +
                    ",name:'"+entries.get("name")+"'" +
                    ",到达机场:'"+entries.get("到达机场")+"'" +
                    ",起始站:'"+entries.get("起始站")+"'" +
                    ",身份证号:'"+entries.get("身份证号")+"'" +
                    ",Name:'"+entries.get("Name")+"'})";
            Neo4jQuery.write(sql,u2->{});
        });
    }
}