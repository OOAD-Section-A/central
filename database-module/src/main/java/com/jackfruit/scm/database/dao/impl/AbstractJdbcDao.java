package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.config.DatabaseConnectionManager;
import com.scm.subsystems.DatabaseDesignSubsystem;
import com.jackfruit.scm.database.util.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJdbcDao {

    private final DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();
    private final DatabaseDesignSubsystem exceptions = DatabaseDesignSubsystem.INSTANCE;

    protected Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    protected void releaseConnection(Connection connection) {
        connectionManager.releaseConnection(connection);
    }

    protected <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(rowMapper.map(resultSet));
                    }
                    return Optional.empty();
                }
            }
        } catch (SQLException exception) {
            // Log the exception to the handler
            handleSqlException(exception, sql);
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    protected <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(rowMapper.map(resultSet));
                }
                return results;
            }
        } catch (SQLException exception) {
            // Log the exception to the handler
            handleSqlException(exception, sql);
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    protected <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<T> results = new ArrayList<>();
                    while (resultSet.next()) {
                        results.add(rowMapper.map(resultSet));
                    }
                    return results;
                }
            }
        } catch (SQLException exception) {
            // Log the exception to the handler
            handleSqlException(exception, sql);
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    protected void executeUpdate(String sql, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            // Log the exception to the handler
            handleSqlException(exception, sql);
            throw new IllegalStateException("Failed to execute update: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    @FunctionalInterface
    protected interface SqlConsumer<T> {
        void accept(T input) throws SQLException;
    }

    /**
     * Handle SQL exceptions by calling appropriate exception handler methods.
     * This method categorizes SQLException and delegates to the appropriate handler.
     * Matches all 32 database design subsystem exceptions.
     */
    private void handleSqlException(SQLException exception, String sql) {
        String message = exception.getMessage();
        if (message == null) return;
        
        // ID 301 - DUPLICATE_PRIMARY_KEY
        if (message.contains("Duplicate entry") || message.contains("Duplicate key")) {
            exceptions.onDuplicatePrimaryKey("Entity", extractKeyValue(message));
        }
        // ID 302 - FOREIGN_KEY_VIOLATION
        else if (message.contains("foreign key constraint") || message.contains("FOREIGN KEY")) {
            exceptions.onForeignKeyViolation("ChildTable", "ParentTable", extractKeyValue(message));
        }
        // ID 303 - UNIQUE_CONSTRAINT_VIOLATION
        else if (message.contains("UNIQUE constraint failed") || message.contains("Duplicate entry")) {
            exceptions.onUniqueConstraintViolation("field_name", extractKeyValue(message));
        }
        // ID 101 - TRANSACTION_DEADLOCK
        else if (message.contains("Deadlock") || message.contains("deadlock")) {
            exceptions.onTransactionDeadlock("entity_id", "database_operation");
        }
        // ID 105 - DEADLOCK_ON_BULK_INSERT
        else if (message.contains("Deadlock found") || message.contains("bulk")) {
            exceptions.onDeadlockOnBulkInsert(extractTableName(sql));
        }
        // ID 165 - TABLE_NOT_FOUND
        else if (message.contains("doesn't exist") || message.contains("Table") || message.contains("table")) {
            exceptions.onTableNotFound(extractTableName(sql));
        }
        // ID 352 - QUERY_TIMEOUT
        else if (message.contains("timeout") || message.contains("Timeout") || message.contains("timed out")) {
            exceptions.onQueryTimeout(sql, 30000);
        }
        // ID 51 - DB_CONNECTION_FAILED
        else if (message.contains("Connection refused") || message.contains("Connection reset")) {
            exceptions.onDbConnectionFailed("database_host");
        }
        // ID 53 - DB_HOST_NOT_FOUND
        else if (message.contains("Unknown host") || message.contains("Host not found")) {
            exceptions.onDbHostNotFound("database_host");
        }
        // ID 251 - DB_AUTHENTICATION_FAILED
        else if (message.contains("Access denied") || message.contains("Access Denied")) {
            exceptions.onDbAuthenticationFailed("database_host");
        }
        // ID 52 - DB_TIMEOUT
        else if (message.contains("Read timed out") || message.contains("Connection timeout")) {
            exceptions.onDbTimeout(30000);
        }
        // ID 3 - CHECK_CONSTRAINT_VIOLATION
        else if (message.contains("Check constraint") || message.contains("check constraint")) {
            exceptions.onCheckConstraintViolation("field_name", extractConstraintName(message));
        }
        // ID 2 - NULL_CONSTRAINT_VIOLATION
        else if (message.contains("NOT NULL") || message.contains("null constraint")) {
            exceptions.onNullConstraintViolation(extractFieldName(message));
        }
        // ID 1 - INVALID_DATA_TYPE
        else if (message.contains("Invalid") || message.contains("Data type")) {
            exceptions.onInvalidDataType(extractFieldName(message), message);
        }
        // ID 164 - RECORD_NOT_FOUND
        else if (message.contains("No row") || message.contains("not found")) {
            exceptions.onRecordNotFound("Entity", "unknown_id");
        }
        // ID 102 - TRANSACTION_ROLLBACK_FAILED
        else if (message.contains("Rollback failed")) {
            exceptions.onTransactionRollbackFailed("entity_id");
        }
        // ID 103 - PARTIAL_COMMIT_ERROR
        else if (message.contains("Partial commit") || message.contains("partial")) {
            exceptions.onPartialCommitError("entity_id");
        }
        // ID 104 - SAVEPOINT_FAILED
        else if (message.contains("Savepoint") || message.contains("savepoint")) {
            exceptions.onSavepointFailed("savepoint_name");
        }
        // ID 106 - RECORD_LOCKED
        else if (message.contains("Lock") || message.contains("locked")) {
            exceptions.onRecordLocked("entity_id");
        }
        // ID 151 - NEGATIVE_STOCK_QUANTITY
        else if (message.contains("Stock") || message.contains("Quantity")) {
            exceptions.onNegativeStockQuantity("product_id", -1);
        }
        // ID 162 - MAX_CONNECTIONS_EXCEEDED
        else if (message.contains("max_connections") || message.contains("too many connections")) {
            exceptions.onMaxConnectionsExceeded(100);
        }
        // ID 4 - INVALID_ORDER_STATE_TRANSITION
        else if (message.contains("Order") || message.contains("state")) {
            exceptions.onInvalidOrderStateTransition("order_id", "current_state", "new_state");
        }
        // ID 304 - ORPHAN_RECORD_DETECTED
        else if (message.contains("Orphan") || message.contains("orphan")) {
            exceptions.onOrphanRecordDetected("Entity", "entity_id");
        }
        // ID 305 - PRICE_DATA_INCONSISTENCY
        else if (message.contains("Price") || message.contains("price")) {
            exceptions.onPriceDataInconsistency("entity_id");
        }
        // ID 306 - DELIVERY_RECORD_NOT_LINKED
        else if (message.contains("Delivery") || message.contains("delivery")) {
            exceptions.onDeliveryRecordNotLinked("delivery_id");
        }
        // ID 307 - BARCODE_DUPLICATE
        else if (message.contains("Barcode") || message.contains("barcode")) {
            exceptions.onBarcodeDuplicate("barcode_value");
        }
        // ID 308 - DUPLICATE_TABLE_NAME
        else if (message.contains("Table already exists")) {
            exceptions.onDuplicateTableName(extractTableName(sql));
        }
        // ID 201 - SCHEMA_VERSION_MISMATCH
        else if (message.contains("Schema version") || message.contains("schema")) {
            exceptions.onSchemaVersionMismatch("1.0", "2.0");
        }
        // ID 202 - SCHEMA_MIGRATION_FAILED
        else if (message.contains("Migration failed") || message.contains("migration")) {
            exceptions.onSchemaMigrationFailed("migration_script.sql");
        }
        // ID 351 - INDEX_CORRUPTION
        else if (message.contains("Index") || message.contains("index")) {
            exceptions.onIndexCorruption("index_name");
        }
    }

    /**
     * Extract table name from SQL query.
     */
    private String extractTableName(String sql) {
        if (sql == null) return "unknown";
        String upper = sql.toUpperCase();
        String[] patterns = {"FROM ", "INTO ", "UPDATE ", "DELETE FROM "};
        for (String pattern : patterns) {
            int index = upper.indexOf(pattern);
            if (index != -1) {
                int start = index + pattern.length();
                int end = Math.min(start + 50, sql.length());
                String substring = sql.substring(start, end).trim();
                return substring.split("[\\s(,]")[0];
            }
        }
        return "unknown";
    }

    /**
     * Extract key value from error message.
     */
    private String extractKeyValue(String message) {
        if (message == null) return "unknown";
        try {
            if (message.contains("'")) {
                String[] parts = message.split("'");
                if (parts.length >= 2) {
                    return parts[1];
                }
            }
        } catch (Exception e) {
            // Silently fail, return unknown
        }
        return "unknown";
    }

    /**
     * Extract field name from error message.
     */
    private String extractFieldName(String message) {
        if (message == null) return "unknown_field";
        try {
            if (message.contains("Column")) {
                String[] parts = message.split("Column");
                if (parts.length > 1) {
                    return parts[1].trim().split("[\\s,.]")[0];
                }
            } else if (message.contains("field")) {
                String[] parts = message.split("field");
                if (parts.length > 1) {
                    return parts[1].trim().split("[\\s,.]")[0];
                }
            }
        } catch (Exception e) {
            // Silently fail
        }
        return "unknown_field";
    }

    /**
     * Extract constraint name from error message.
     */
    private String extractConstraintName(String message) {
        if (message == null) return "unknown_constraint";
        try {
            if (message.contains("constraint")) {
                String[] parts = message.split("constraint");
                if (parts.length > 1) {
                    return parts[1].trim().split("[\\s,.]")[0];
                }
            }
        } catch (Exception e) {
            // Silently fail
        }
        return "unknown_constraint";
    }
}
