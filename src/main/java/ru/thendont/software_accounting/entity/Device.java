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
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<SoftwareInstallation> installations = new ArrayList<>();

    public Device() {

    }

    public Device(Long id, String title, String osTitle, String ipAddress, Integer ramSize, Classroom classroom) {
        this.id = id;
        this.title = title;
        this.osTitle = osTitle;
        this.ipAddress = ipAddress;
        this.ramSize = ramSize;
        this.classroom = classroom;
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

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
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

    public Classroom getClassroom() {
        return classroom;
    }

    public List<SoftwareInstallation> getInstallations() {
        return installations;
    }
}