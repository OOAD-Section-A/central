package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.ExceptionLogDao;
import com.jackfruit.scm.database.model.SubsystemException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class ExceptionLogDaoImpl extends AbstractJdbcDao implements ExceptionLogDao {

    @Override
    public void save(SubsystemException subsystemException) {
        executeUpdate(
                """
                INSERT INTO SCM_EXCEPTION_LOG
                (exception_id, exception_name, severity, subsystem, error_message, logged_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setInt(1, subsystemException.getExceptionId());
                    statement.setString(2, subsystemException.getExceptionName());
                    statement.setString(3, subsystemException.getSeverity());
                    statement.setString(4, subsystemException.getSubsystem());
                    statement.setString(5, subsystemException.getErrorMessage());
                    statement.setTimestamp(6, Timestamp.valueOf(subsystemException.getLoggedAt()));
                });
    }

    @Override
    public void update(SubsystemException subsystemException) {
        executeUpdate(
                """
                UPDATE SCM_EXCEPTION_LOG
                SET exception_name = ?, severity = ?, subsystem = ?, error_message = ?, logged_at = ?
                WHERE id = ?
                """,
                statement -> {
                    statement.setString(1, subsystemException.getExceptionName());
                    statement.setString(2, subsystemException.getSeverity());
                    statement.setString(3, subsystemException.getSubsystem());
                    statement.setString(4, subsystemException.getErrorMessage());
                    statement.setTimestamp(5, Timestamp.valueOf(subsystemException.getLoggedAt()));
                    statement.setLong(6, subsystemException.getId());
                });
    }

    @Override
    public Optional<SubsystemException> findById(String exceptionId) {
        return queryForObject(
                "SELECT * FROM SCM_EXCEPTION_LOG WHERE exception_id = ?",
                resultSet -> new SubsystemException(
                        resultSet.getLong("id"),
                        resultSet.getInt("exception_id"),
                        resultSet.getString("exception_name"),
                        resultSet.getString("severity"),
                        resultSet.getString("subsystem"),
                        resultSet.getString("error_message"),
                        resultSet.getTimestamp("logged_at").toLocalDateTime()),
                statement -> statement.setInt(1, Integer.parseInt(exceptionId)));
    }

    @Override
    public List<SubsystemException> findAll() {
        return queryForList(
                "SELECT * FROM SCM_EXCEPTION_LOG",
                resultSet -> new SubsystemException(
                        resultSet.getLong("id"),
                        resultSet.getInt("exception_id"),
                        resultSet.getString("exception_name"),
                        resultSet.getString("severity"),
                        resultSet.getString("subsystem"),
                        resultSet.getString("error_message"),
                        resultSet.getTimestamp("logged_at").toLocalDateTime()));
    }
}
