package com.jackfruit.scm.database.config;

import com.scm.subsystems.DatabaseDesignSubsystem;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class DatabaseConnectionManager {

    private static volatile DatabaseConnectionManager instance;
    private final DatabaseConfig config;
    private final BlockingQueue<Connection> pool;
    private final DatabaseDesignSubsystem exceptions = DatabaseDesignSubsystem.INSTANCE;

    private DatabaseConnectionManager(DatabaseConfig config) {
        this.config = config;
        this.pool = new ArrayBlockingQueue<>(config.getPoolSize());
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager(DatabaseConfig.load());
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection pooled = pool.poll();
        if (pooled != null && !pooled.isClosed()) {
            return pooled;
        }
        try {
            return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            handleConnectionException(e);
            throw e;
        }
    }

    /**
     * Handle connection-related exceptions.
     * Covers: DB_CONNECTION_FAILED, DB_TIMEOUT, DB_HOST_NOT_FOUND, 
     *         DB_AUTHENTICATION_FAILED, TABLE_NOT_FOUND, MAX_CONNECTIONS_EXCEEDED
     */
    private void handleConnectionException(SQLException e) {
        String message = e.getMessage();
        String host = extractHost(config.getUrl());
        
        if (message != null) {
            // ID 251 - DB_AUTHENTICATION_FAILED
            if (message.contains("Access denied")) {
                exceptions.onDbAuthenticationFailed(host);
            }
            // ID 165 - TABLE_NOT_FOUND
            else if (message.contains("Unknown database")) {
                exceptions.onTableNotFound(config.getUrl());
            }
            // ID 53 - DB_HOST_NOT_FOUND
            else if (message.contains("Unknown host") || message.contains("Host not found")) {
                exceptions.onDbHostNotFound(host);
            }
            // ID 162 - MAX_CONNECTIONS_EXCEEDED
            else if (message.contains("max_connections") || message.contains("too many connections")) {
                exceptions.onMaxConnectionsExceeded(100);
            }
            // ID 52 - DB_TIMEOUT
            else if (message.contains("timeout") || message.contains("Timeout") || message.contains("timed out")) {
                exceptions.onDbTimeout(30000);
            }
            // ID 51 - DB_CONNECTION_FAILED (catch-all)
            else if (message.contains("Connection refused") || message.contains("Connection reset")) {
                exceptions.onDbConnectionFailed(host);
            }
            // ID 51 - DB_CONNECTION_FAILED (default)
            else {
                exceptions.onDbConnectionFailed(host);
            }
        } else {
            // Default: connection failed
            exceptions.onDbConnectionFailed(host);
        }
    }

    private String extractHost(String url) {
        try {
            // Extract host from JDBC URL: jdbc:mysql://hostname:port/database
            if (url != null && url.contains("://")) {
                String afterProtocol = url.split("://")[1];
                return afterProtocol.split(":")[0];
            }
        } catch (Exception e) {
            // If extraction fails, return the full URL
        }
        return url != null ? url : "unknown_host";
    }

    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            if (connection.isClosed()) {
                return;
            }
            if (!pool.offer(connection)) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    public synchronized void shutdown() {
        List<Connection> connections = new ArrayList<>();
        pool.drainTo(connections);
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}
