-- Compatibility patch for scm-exception-handler-v3.jar
-- Run this after schema.sql is applied.

USE OOAD;

CREATE TABLE IF NOT EXISTS SCM_EXCEPTION_LOG (
    id BIGINT NOT NULL AUTO_INCREMENT,
    exception_id INT NOT NULL,
    exception_name VARCHAR(150) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    subsystem VARCHAR(100) NOT NULL,
    error_message VARCHAR(500) NOT NULL,
    logged_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_scm_exception_log_subsystem_logged_at (subsystem, logged_at),
    INDEX idx_scm_exception_log_exception_id (exception_id)
);
