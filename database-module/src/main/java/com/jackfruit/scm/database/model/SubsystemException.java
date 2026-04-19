package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public class SubsystemException {

    private Long id;
    private Integer exceptionId;
    private String exceptionName;
    private String severity;
    private String subsystem;
    private String errorMessage;
    private LocalDateTime loggedAt;

    public SubsystemException() {
    }

    public SubsystemException(Long id, Integer exceptionId, String exceptionName,
                              String severity, String subsystem, String errorMessage,
                              LocalDateTime loggedAt) {
        this.id = id;
        this.exceptionId = exceptionId;
        this.exceptionName = exceptionName;
        this.severity = severity;
        this.subsystem = subsystem;
        this.errorMessage = errorMessage;
        this.loggedAt = loggedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }
}
