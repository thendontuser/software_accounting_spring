package ru.thendont.software_accounting.audit.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Класс-сущность таблицы audit.audit_deleted_archive из БД
 * @author thendont
 * @version 1.0
 */
@Entity
@Table(schema = "audit", name = "audit_deleted_archive")
public class AuditDeletedArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_seq")
    @SequenceGenerator(name = "audit_log_seq", sequenceName = "audit.audit_deleted_archive_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "deleted_data", columnDefinition = "jsonb", nullable = false)
    private String deletedData;

    @Column(name = "deleted_by", nullable = false)
    private Long deletedBy;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;

    public AuditDeletedArchive() {

    }

    public AuditDeletedArchive(Long id,
                               String entityName,
                               String entityId,
                               String deletedData,
                               Long deletedBy,
                               LocalDateTime deletedAt) {
        this.id = id;
        this.entityName = entityName;
        this.entityId = entityId;
        this.deletedData = deletedData;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDeletedData(String deletedData) {
        this.deletedData = deletedData;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getDeletedData() {
        return deletedData;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}