package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.dao.ForecastTimeseriesDao;
import com.jackfruit.scm.database.model.ForecastTimeseries;
import com.jackfruit.scm.database.model.UiModels.UiAuditLog;
import com.jackfruit.scm.database.model.UiModels.UiNotification;
import com.jackfruit.scm.database.model.UiModels.UiPanelState;
import com.jackfruit.scm.database.model.UiModels.UiSession;
import com.jackfruit.scm.database.model.UiModels.UiUser;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class UiSubsystemFacade {

    private final JdbcOperations jdbcOperations;
    private final ForecastTimeseriesDao forecastTimeseriesDao;

    public UiSubsystemFacade(JdbcOperations jdbcOperations, ForecastTimeseriesDao forecastTimeseriesDao) {
        this.jdbcOperations = jdbcOperations;
        this.forecastTimeseriesDao = forecastTimeseriesDao;
    }

    public void createUser(UiUser user) {
        jdbcOperations.update(
                """
                INSERT INTO ui_users
                (username, password_hash, two_factor_token, user_role, authorized_menu_items, is_account_locked,
                 login_attempt_count, last_login_timestamp, user_email, user_display_name, theme_preference,
                 language_preference, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, user.username());
                    statement.setString(2, user.passwordHash());
                    if (user.twoFactorToken() == null) {
                        statement.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(3, user.twoFactorToken());
                    }
                    statement.setString(4, user.userRole());
                    statement.setString(5, user.authorizedMenuItems());
                    statement.setBoolean(6, user.accountLocked());
                    statement.setInt(7, user.loginAttemptCount());
                    statement.setTimestamp(8, user.lastLoginTimestamp() == null ? null : Timestamp.valueOf(user.lastLoginTimestamp()));
                    statement.setString(9, user.userEmail());
                    statement.setString(10, user.displayName());
                    statement.setString(11, user.themePreference());
                    statement.setString(12, user.languagePreference());
                    statement.setTimestamp(13, Timestamp.valueOf(user.createdAt()));
                });
    }

    public List<UiUser> listUsers() {
        return jdbcOperations.query(
                "SELECT * FROM ui_users",
                resultSet -> new UiUser(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        resultSet.getObject("two_factor_token") == null ? null : resultSet.getInt("two_factor_token"),
                        null,
                        resultSet.getString("user_role"),
                        resultSet.getString("authorized_menu_items"),
                        0L,
                        resultSet.getInt("login_attempt_count"),
                        resultSet.getBoolean("is_account_locked"),
                        null,
                        resultSet.getTimestamp("last_login_timestamp") == null ? null : resultSet.getTimestamp("last_login_timestamp").toLocalDateTime(),
                        resultSet.getString("user_email"),
                        resultSet.getString("user_display_name"),
                        resultSet.getString("theme_preference"),
                        resultSet.getString("language_preference"),
                        null,
                        null,
                        null,
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createSession(UiSession session) {
        jdbcOperations.update(
                "INSERT INTO ui_sessions (user_id, jwt_session_token, redirect_panel_url, session_expiry_time, session_status, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setInt(1, session.userId());
                    statement.setString(2, session.jwtSessionToken());
                    statement.setString(3, session.redirectPanelUrl());
                    statement.setLong(4, session.sessionExpiryTime());
                    statement.setString(5, session.sessionStatus());
                    statement.setTimestamp(6, Timestamp.valueOf(session.createdAt()));
                });
    }

    public void createNotification(UiNotification notification) {
        jdbcOperations.update(
                "INSERT INTO ui_notifications (user_id, notification_type, notification_message, is_read, created_at) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setInt(1, notification.userId());
                    statement.setString(2, notification.notificationType());
                    statement.setString(3, notification.notificationMessage());
                    statement.setBoolean(4, notification.read());
                    statement.setTimestamp(5, Timestamp.valueOf(notification.createdAt()));
                });
    }

    public List<UiNotification> listNotifications() {
        return jdbcOperations.query(
                "SELECT * FROM ui_notifications",
                resultSet -> new UiNotification(
                        resultSet.getInt("notification_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("notification_type"),
                        resultSet.getString("notification_message"),
                        resultSet.getBoolean("is_read"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createAuditLog(UiAuditLog auditLog) {
        jdbcOperations.update(
                "INSERT INTO ui_audit_log (audit_timestamp, audit_action_user, audit_action_description, audit_module_name) VALUES (?, ?, ?, ?)",
                statement -> {
                    statement.setTimestamp(1, Timestamp.valueOf(auditLog.auditTimestamp()));
                    statement.setString(2, auditLog.auditActionUser());
                    statement.setString(3, auditLog.auditActionDescription());
                    statement.setString(4, auditLog.auditModuleName());
                });
    }

    public void createPanelState(UiPanelState panelState) {
        jdbcOperations.update(
                """
                INSERT INTO ui_panel_state
                (panel_id, user_id, notification_count, current_panel_state, breadcrumb_trail, sidebar_menu_items, active_user_role)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, panelState.panelId());
                    statement.setInt(2, panelState.userId());
                    if (panelState.notificationCount() == null) {
                        statement.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(3, panelState.notificationCount());
                    }
                    statement.setString(4, panelState.currentPanelState());
                    statement.setString(5, panelState.breadcrumbTrail());
                    statement.setString(6, panelState.sidebarMenuItems());
                    statement.setString(7, panelState.activeUserRole());
                });
    }

    public List<ForecastTimeseries> listForecastTimeseriesForForecast(String forecastId) {
        return forecastTimeseriesDao.findByForecastId(forecastId);
    }

    public ForecastTimeseries getForecastTimeseries(String timeseriesId) {
        return forecastTimeseriesDao.findById(timeseriesId).orElse(null);
    }

    public List<ForecastTimeseries> listAllForecastTimeseries() {
        return forecastTimeseriesDao.findAll();
    }
}
