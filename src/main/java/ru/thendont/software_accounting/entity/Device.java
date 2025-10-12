package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "os_title")
    private String osTitle;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "ram_size")
    private Integer ramSize;

    @ManyToOne
    @JoinColumn(name = "dep_number")
    private Department department;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<InstallationRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<SoftwareInstallation> installations = new ArrayList<>();

    public Device() {

    }

    public Device(Long id, String title, String osTitle, String ipAddress, Integer ramSize, Department department) {
        this.id = id;
        this.title = title;
        this.osTitle = osTitle;
        this.ipAddress = ipAddress;
        this.ramSize = ramSize;
        this.department = department;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOsTitle(String osTitle) {
        this.osTitle = osTitle;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setRamSize(Integer ramSize) {
        this.ramSize = ramSize;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOsTitle() {
        return osTitle;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getRamSize() {
        return ramSize;
    }

    public Department getDepartment() {
        return department;
    }

    public List<InstallationRequest> getRequests() {
        return requests;
    }

    public List<SoftwareInstallation> getInstallations() {
        return installations;
    }
}