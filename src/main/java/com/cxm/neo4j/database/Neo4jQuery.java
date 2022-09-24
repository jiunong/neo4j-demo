package com.cxm.neo4j.database;

import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.function.Consumer;

import static org.neo4j.driver.Values.parameters;

public class Neo4jQuery {

    private static final Session session = Neo4jJavaJdbc.getSession();

    public static void query(String cypher, Consumer<Neo4jResultWrapper> consumer) {
        Result result = session.run(cypher);
        consumer.accept(new Neo4jResultWrapper(result));
        session.close();
    }

    /**
     *  Creates a new query
     *  neo4j查询功能
     * @param cypher
     * @param consumer
     * @param kavs
     */
    public static void query(String cypher, Consumer<Neo4jResultWrapper> consumer, Object... kavs) {
        session.readTransaction(transaction -> {
            consumer.accept(new Neo4jResultWrapper(transaction.run(cypher, parameters(kavs))));
            return null;
        });
        session.close();
    }

    /**
     *  Creates a new query
     *  neo4j写入功能
     * @param cypher
     * @param consumer
     * @param kavs
     */
    public static void write(String cypher, Consumer<Neo4jResultWrapper> consumer, Object... kavs) {
        session.writeTransaction(transaction -> {
            consumer.accept(new Neo4jResultWrapper(transaction.run(cypher, parameters(kavs))));
            return null;
        });
    }

}
