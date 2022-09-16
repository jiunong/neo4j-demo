package com.cxm.neo4j.database;


import java.sql.Connection;
import java.sql.DriverManager;

public class Neo4jJdbc {


    public static final String NEO4J_JDBC_URL = "jdbc:neo4j://127.0.0.1:7474";
    public static final String NEO4J_USER = "neo4j";
    public static final String NEO4J_PASSWORD = "cxm123456";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.neo4j.jdbc.Neo4jDriver").newInstance();
                connection = DriverManager.getConnection(NEO4J_JDBC_URL, NEO4J_USER, NEO4J_PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

}

