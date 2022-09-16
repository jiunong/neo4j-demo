package com.cxm.neo4j.database;

import org.neo4j.driver.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

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
