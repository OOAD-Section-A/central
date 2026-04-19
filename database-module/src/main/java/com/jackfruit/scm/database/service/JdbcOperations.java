package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.config.DatabaseConnectionManager;
import com.jackfruit.scm.database.util.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcOperations {

    private final DatabaseConnectionManager connectionManager;

    public JdbcOperations() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    public void update(String sql, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = connectionManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to execute update: " + sql, exception);
        } finally {
            connectionManager.releaseConnection(connection);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper) {
        Connection connection = null;
        try {
            connection = connectionManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(mapper.map(resultSet));
                }
                return results;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            connectionManager.releaseConnection(connection);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = connectionManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<T> results = new ArrayList<>();
                    while (resultSet.next()) {
                        results.add(mapper.map(resultSet));
                    }
                    return results;
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            connectionManager.releaseConnection(connection);
        }
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, SqlConsumer<PreparedStatement> binder) {
        List<T> results = query(sql, mapper, binder);
        return results.stream().findFirst();
    }

    @FunctionalInterface
    public interface SqlConsumer<T> {
        void accept(T input) throws SQLException;
    }
}
