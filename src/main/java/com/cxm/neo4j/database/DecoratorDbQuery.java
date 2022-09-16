package com.cxm.neo4j.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

@Deprecated
public class DecoratorDbQuery extends AbstractDbQuery {


    private Connection connection;

    public DecoratorDbQuery(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        Optional.ofNullable(connection).ifPresent((con) -> {
            try {
                con.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }

    public void execute(PreparedStatement preparedStatement, Consumer<Boolean> consumer) throws SQLException {
        consumer.accept(preparedStatement.execute());
        closeConnection();
    }

    public void query(PreparedStatement preparedStatement, Consumer<ResultSetWrapper> consumer) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                consumer.accept(new ResultSetWrapper(resultSet));
            }
        }
        closeConnection();
    }

    public static class ResultSetWrapper {

        private ResultSet resultSet;


        private int i;

        ResultSetWrapper(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        ResultSetWrapper(int i) {
            this.i = i;
        }

        public ResultSet getResultSet() {
            return resultSet;
        }

        public String getStringResult(String columnLabel) {
            try {
                return resultSet.getString(columnLabel);
            } catch (SQLException sqlException) {
                throw new RuntimeException(String.format("读取[%s]字段出错!", columnLabel), sqlException);
            }
        }

        public String getStringResult(int columnIndex) {
            try {
                return resultSet.getString(columnIndex);
            } catch (SQLException sqlException) {
                throw new RuntimeException(String.format("读取[%s]字段出错!", columnIndex), sqlException);
            }
        }

    }
}
