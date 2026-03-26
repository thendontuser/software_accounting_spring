package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;
import ru.thendont.software_accounting.service.enums.LicenseRequestStatus;

import java.time.LocalDate;

/**
 * Класс-сущность таблицы license_request из БД
 * @author thendont
 * @version 1.0
 */
@Entity
@Table(name = "license_request")
public class LicenseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "software_id")
    private Software software;

    @Column(name = "amount")
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private LicenseRequestStatus status;

    @Column(name = "comment")
    private String comment;

    public LicenseRequest() {

    }

    public LicenseRequest(Long id,
                          Software software,
                          Integer amount,
                          User requestedBy,
                          LocalDate createdAt,
                          LicenseRequestStatus status,
                          String comment) {
        this.id = id;
        this.software = software;
        this.amount = amount;
        this.requestedBy = requestedBy;
        this.createdAt = createdAt;
        this.status = status;
        this.comment = comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(LicenseRequestStatus status) {
        this.status = status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Software getSoftware() {
        return software;
    }

    public Integer getAmount() {
        return amount;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LicenseRequestStatus getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }
}