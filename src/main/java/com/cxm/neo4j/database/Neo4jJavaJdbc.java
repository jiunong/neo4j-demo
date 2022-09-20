package com.cxm.neo4j.database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class Neo4jJavaJdbc {

    private static final String URI = "bolt://localhost:7687/";
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "cxm123456";

    private static Session session = null;

    public static Session getSession() {
        Driver driver = GraphDatabase.driver(URI, AuthTokens.basic(USERNAME, PASSWORD));
        if (session == null) {
            session = driver.session();
        }
        return session;
    }

}
