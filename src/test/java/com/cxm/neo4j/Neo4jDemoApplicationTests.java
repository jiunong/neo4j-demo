package com.cxm.neo4j;

import com.alibaba.excel.EasyExcel;
import com.cxm.neo4j.database.DecoratorDbQuery;
import com.cxm.neo4j.database.Neo4jJdbc;
import com.cxm.neo4j.model.Person;
import com.cxm.neo4j.util.GeneratePersonInfoCsv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class Neo4jDemoApplicationTests {

    @Test
    void contextLoads() {
        List<Person> persons = GeneratePersonInfoCsv.getPersons(100);
        EasyExcel.write("/Users/mac/Downloads/demo.csv", Person.class).sheet("模板").doWrite(persons);
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

}
