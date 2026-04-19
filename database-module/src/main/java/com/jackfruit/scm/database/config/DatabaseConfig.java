package com.jackfruit.scm.database.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class DatabaseConfig {

    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    private DatabaseConfig(String url, String username, String password, int poolSize) {
        this.url = Objects.requireNonNull(url, "Database URL cannot be null");
        this.username = Objects.requireNonNull(username, "Database username cannot be null");
        this.password = Objects.requireNonNull(password, "Database password cannot be null");
        this.poolSize = poolSize;
    }

    public static DatabaseConfig load() {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("database.properties is missing from the classpath");
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load database.properties", exception);
        }

        return new DatabaseConfig(
                resolveSetting("db.url", "DB_URL", properties, "jdbc:mysql://localhost:3306/OOAD"),
                resolveSetting("db.username", "DB_USERNAME", properties, "root"),
                resolveSetting("db.password", "DB_PASSWORD", properties, ""),
                Integer.parseInt(resolveSetting("db.pool.size", "DB_POOL_SIZE", properties, "5"))
        );
    }

    private static String resolveSetting(String propertyKey, String envKey, Properties properties, String defaultValue) {
        String systemValue = System.getProperty(propertyKey);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String propertyValue = properties.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        return defaultValue;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
