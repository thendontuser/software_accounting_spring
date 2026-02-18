package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "installation_report")
public class InstallationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "installation_task_id")
    private InstallationTask installationTask;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "license_required")
    private Boolean licenseRequired;

    @Column(name = "notes")
    private String notes;

    public InstallationReport() {

    }

    public InstallationReport(Long id, InstallationTask installationTask, LocalDate createdAt, Boolean licenseRequired, String notes) {
        this.id = id;
        this.installationTask = installationTask;
        this.createdAt = createdAt;
        this.licenseRequired = licenseRequired;
        this.notes = notes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInstallationTask(InstallationTask installationTask) {
        this.installationTask = installationTask;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setLicenseRequired(Boolean licenseRequired) {
        this.licenseRequired = licenseRequired;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public InstallationTask getInstallationTask() {
        return installationTask;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public Boolean getLicenseRequired() {
        return licenseRequired;
    }

    public String getNotes() {
        return notes;
    }
}