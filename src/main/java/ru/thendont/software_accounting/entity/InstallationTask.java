package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;
import ru.thendont.software_accounting.service.enums.InstallationTaskStatus;

import java.time.LocalDate;

@Entity
@Table(name = "installation_task")
public class InstallationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "installation_request_id")
    private InstallationRequest installationRequest;

    @OneToOne
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @OneToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "status")
    private InstallationTaskStatus status;

    @Column(name = "deadline")
    private LocalDate deadline;

    public InstallationTask() {

    }

    public InstallationTask(Long id,
                            InstallationRequest installationRequest,
                            User assignedBy,
                            User assignedTo,
                            LocalDate createdAt,
                            InstallationTaskStatus status,
                            LocalDate deadline) {
        this.id = id;
        this.installationRequest = installationRequest;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.status = status;
        this.deadline = deadline;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInstallationRequest(InstallationRequest installationRequest) {
        this.installationRequest = installationRequest;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(InstallationTaskStatus status) {
        this.status = status;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Long getId() {
        return id;
    }

    public InstallationRequest getInstallationRequest() {
        return installationRequest;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public InstallationTaskStatus getStatus() {
        return status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
}