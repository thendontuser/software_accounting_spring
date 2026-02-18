package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;
import ru.thendont.software_accounting.service.enums.LicenseRequestStatus;

import java.time.LocalDate;

@Entity
@Table(name = "license_request")
public class LicenseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "installation_report_id")
    private InstallationReport installationReport;

    @ManyToOne
    @JoinColumn(name = "software_id")
    private Software software;

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "status")
    private LicenseRequestStatus status;

    @Column(name = "comment")
    private String comment;

    public LicenseRequest() {

    }

    public LicenseRequest(Long id,
                          InstallationReport installationReport,
                          Software software,
                          User requestedBy,
                          LocalDate createdAt,
                          LicenseRequestStatus status,
                          String comment) {
        this.id = id;
        this.installationReport = installationReport;
        this.software = software;
        this.requestedBy = requestedBy;
        this.createdAt = createdAt;
        this.status = status;
        this.comment = comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInstallationReport(InstallationReport installationReport) {
        this.installationReport = installationReport;
    }

    public void setSoftware(Software software) {
        this.software = software;
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

    public InstallationReport getInstallationReport() {
        return installationReport;
    }

    public Software getSoftware() {
        return software;
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