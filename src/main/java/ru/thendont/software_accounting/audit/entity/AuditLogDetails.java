package ru.thendont.software_accounting.audit.entity;

import jakarta.persistence.*;

/**
 * Класс-сущность таблицы audit.audit_log_details из БД
 * @author thendont
 * @version 1.0
 */
@Entity
@Table(schema = "audit", name = "audit_log_details")
public class AuditLogDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_seq")
    @SequenceGenerator(name = "audit_log_seq", sequenceName = "audit.audit_log_details_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "audit_log_id")
    private AuditLog auditLog;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    public AuditLogDetails() {

    }

    public AuditLogDetails(Long id, AuditLog auditLog, String fieldName, String oldValue, String newValue) {
        this.id = id;
        this.auditLog = auditLog;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuditLog(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getId() {
        return id;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}