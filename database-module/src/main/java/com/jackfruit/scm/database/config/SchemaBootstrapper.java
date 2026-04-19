package com.jackfruit.scm.database.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public final class SchemaBootstrapper {

    private static final String CORE_TABLE = "orders";

    private SchemaBootstrapper() {
    }

    public static void ensureSchemaInitialized() {
        DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();
        Connection connection = null;
        try {
            connection = connectionManager.getConnection();
            if (hasTable(connection, CORE_TABLE)) {
                return;
            }
            applySchema(connection);
        } catch (SQLException | IOException exception) {
            throw new IllegalStateException("Unable to initialize schema from schema.sql", exception);
        } finally {
            connectionManager.releaseConnection(connection);
        }
    }

    private static boolean hasTable(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (var resultSet = metaData.getTables(connection.getCatalog(), null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private static void applySchema(Connection connection) throws IOException, SQLException {
        StringBuilder statementBuffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openSchemaStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                statementBuffer.append(line).append(System.lineSeparator());
                if (trimmed.endsWith(";")) {
                    executeStatement(connection, statementBuffer.toString());
                    statementBuffer.setLength(0);
                }
            }
        }
    }

    private static InputStream openSchemaStream() throws IOException {
        InputStream inputStream = SchemaBootstrapper.class.getClassLoader().getResourceAsStream("schema.sql");
        if (inputStream == null) {
            throw new IOException("schema.sql is missing from the classpath");
        }
        return inputStream;
    }

    private static void executeStatement(Connection connection, String rawStatement) throws SQLException {
        String statementText = rawStatement.trim();
        if (statementText.endsWith(";")) {
            statementText = statementText.substring(0, statementText.length() - 1).trim();
        }
        if (statementText.isEmpty()) {
            return;
        }

        String upper = statementText.toUpperCase(Locale.ROOT);
        if (upper.startsWith("CREATE DATABASE") || upper.startsWith("USE ")) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(statementText);
        }
    }
}
