package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "software_installation")
public class SoftwareInstallation {

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

    @Column(name = "i_date")
    private LocalDate date;

    public SoftwareInstallation() {

    }

    public SoftwareInstallation(Long id, Software software, Device device, User user, LocalDate date) {
        this.id = id;
        this.software = software;
        this.device = device;
        this.user = user;
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
}