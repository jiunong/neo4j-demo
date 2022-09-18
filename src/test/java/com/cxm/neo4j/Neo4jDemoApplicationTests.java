package com.cxm.neo4j;

import com.alibaba.excel.EasyExcel;
import com.cxm.neo4j.database.DecoratorDbQuery;
import com.cxm.neo4j.database.Neo4jJdbc;
import com.cxm.neo4j.database.Neo4jQuery;
import com.cxm.neo4j.model.Person;
import com.cxm.neo4j.util.GeneratePersonInfoCsv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

}