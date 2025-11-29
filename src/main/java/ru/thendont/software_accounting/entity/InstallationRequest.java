package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "installation_request")
public class InstallationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "software_id")
    private Software software;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "request_date")
    private LocalDate date;

    @Column(name = "status")
    private String status;

    @Column(name = "comment")
    private String comment;

    public InstallationRequest() {

    }

    public InstallationRequest(Long id, Software software, Device device, User user, LocalDate date,
                               String status, String comment) {
        this.id = id;
        this.software = software;
        this.device = device;
        this.user = user;
        this.date = date;
        this.status = status;
        this.comment = comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(String status) {
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

    public Device getDevice() {
        return device;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }
}