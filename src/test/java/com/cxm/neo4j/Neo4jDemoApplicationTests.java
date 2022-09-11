package com.cxm.neo4j;

import com.alibaba.excel.EasyExcel;
import com.cxm.neo4j.model.Person;
import com.cxm.neo4j.util.GeneratePersonInfoCsv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Neo4jDemoApplicationTests {

    @Test
    void contextLoads() {

        List<Person> persons = GeneratePersonInfoCsv.getPersons(100);
        EasyExcel.write("/Users/mac/Downloads/demo.csv", Person.class).sheet("模板").doWrite(persons);

    }

}
