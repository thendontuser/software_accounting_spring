package ru.thendont.software_accounting.audit.entity;

import jakarta.persistence.*;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.enums.UserRoles;

import java.time.LocalDateTime;

/**
 * Класс-сущность таблицы audit.audit_log из БД
 * @author thendont
 * @version 1.0
 */
@Entity
@Table(schema = "audit", name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_seq")
    @SequenceGenerator(name = "audit_log_seq", sequenceName = "audit.audit_log_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_id")
    private String entityId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_role")
    private UserRoles userRole;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public AuditLog() {

    }

    public AuditLog(Long id,
                    String actionType,
                    String entityName,
                    String entityId,
                    User user,
                    UserRoles userRole,
                    LocalDateTime timestamp) {
        this.id = id;
        this.actionType = actionType;
        this.entityName = entityName;
        this.entityId = entityId;
        this.user = user;
        this.userRole = userRole;
        this.timestamp = timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserRole(UserRoles userRole) {
        this.userRole = userRole;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getActionType() {
        return actionType;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public User getUser() {
        return user;
    }

    public UserRoles getUserRole() {
        return userRole;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}