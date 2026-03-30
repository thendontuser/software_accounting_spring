package ru.thendont.software_accounting.audit.entity;

import jakarta.persistence.*;
import ru.thendont.software_accounting.entity.User;

import java.time.LocalDateTime;

/**
 * Класс-сущность таблицы audit.audit_user_session из БД
 * @author thendont
 * @version 1.0
 */
@Entity
@Table(schema = "audit", name = "audit_user_session")
public class AuditUserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "is_active")
    private Boolean isActive;

    public AuditUserSession() {

    }

    public AuditUserSession(Long id,
                            User user,
                            String sessionId,
                            LocalDateTime loginTime,
                            LocalDateTime logoutTime,
                            Boolean isActive) {
        this.id = id;
        this.user = user;
        this.sessionId = sessionId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.isActive = isActive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public Boolean getActive() {
        return isActive;
    }
}