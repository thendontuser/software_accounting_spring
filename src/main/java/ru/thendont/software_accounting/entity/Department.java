package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dep_number")
    private Long depNumber;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Device> devices = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<User> users = new ArrayList<>();

    public Department() {

    }

    public Department(Long depNumber, String title) {
        this.depNumber = depNumber;
        this.title = title;
    }

    public void setDepNumber(Long depNumber) {
        this.depNumber = depNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDepNumber() {
        return depNumber;
    }

    public String getTitle() {
        return title;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public List<User> getUsers() {
        return users;
    }
}