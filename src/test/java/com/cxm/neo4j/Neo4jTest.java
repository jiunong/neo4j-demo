package com.cxm.neo4j;


import com.cxm.neo4j.database.Neo4jJavaJdbc;
import com.cxm.neo4j.database.Neo4jQuery;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.neo4j.driver.Values.parameters;

/**
 * @ClassName Neo4jTest
 * @Deacription java操作neo4j
 * @Author GoldenStar
 * @Date 2021/10/13 14:18
 * @Version 1.0
 **/
@SpringBootTest
public class Neo4jTest {
    @Test
    void main0() {


      /*  Neo4jQuery.query("MATCH (n) return n.姓名 as name",u->{
            while (u.getResultSet().hasNext()){
                System.out.println(u.getResultSet().next().get("name").asString());
            }
        });*/


        Neo4jQuery.write("MATCH (n:Person) where n.姓 = $name and n.性别=$sex return n.姓名 as name1",u->{
            while (u.getResultSet().hasNext()){
                System.out.println(u.getResultSet().next().get("name1").asString());
            }
        },"name","家","sex","男");


        // 释放资源
        //session.close();
        //driver.close();
    }

    /**
     * 根据id查询名称
     *
     * @param session session
     * @param id      id
     * @return name 名称
     */
    private static String getPersonById(Session session, long id) {
        return session.readTransaction(transaction -> {
            Result result = transaction.run("MATCH (n:person) WHERE id(n) = $id return n.name", parameters("id", id));
            return result.single().get(0).asString();
        });

    }

    /**
     * 查询person
     *
     * @param session session
     */
    private static void queryPerson(Session session) {
        Result result = session.run("MATCH (n:person) " +
                "RETURN id(n) as id," +
                "n.name as name," +
                "n.actor as actor");
        while (result.hasNext()) {
            Record record = result.next();
            int id = record.get("id").asInt();
            String name = record.get("name").asString();
            String actor = record.get("actor").asString();
            System.out.println("id：" + id + "，name：" + name + "，actor：" + actor);
        }
    }

    /**
     * 根据id修改名称
     *
     * @param session session
     * @param id      id
     * @param name    name
     */
    private static void updatePerson(Session session, long id, String name) {
        // 执行修改
        session.writeTransaction(transaction -> {
            transaction.run("MATCH (n) WHERE id(n) = $id SET n.name = $name", parameters("id", id, "name", name));
            return null;
        });
    }

    /**
     * 根据id删除person
     *
     * @param session session
     * @param id      id
     */
    private static void deletePerson(Session session, long id) {

        Neo4jQuery.query("DELETE FROM",u->{
            System.out.println(u.getResultSet());
        },"id","1");
        // 执行添加
        session.writeTransaction(transaction -> {
            transaction.run("MATCH (n) WHERE id(n) = $id DELETE n", parameters("id", id));
            return null;
        });
    }

    /**
     * 添加person
     *
     * @param session session
     * @param name    名称
     * @return id
     */
    private static long createPerson(Session session, String name) {
        // 执行添加
        session.writeTransaction(transaction -> {
            transaction.run("CREATE (a:person {name: $name})", parameters("name", name));
            return null;
        });

        return session.readTransaction(transaction -> {
            Result result = transaction.run("MATCH (a:person {name: $name}) RETURN id(a)", parameters("name", name));
            return result.single().get(0).asLong();
        });
    }

    /**
     * 统计
     *
     * @param session session
     * @param query   查询语句
     * @return 数量
     */
    private static int count(Session session, String query) {
        return session.readTransaction(transaction -> transaction.run(query, parameters()).single().get(0).asInt());
    }
}
