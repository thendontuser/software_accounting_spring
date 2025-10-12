package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "developer")
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "company_type")
    private String companyType;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Software> software = new ArrayList<>();

    public Developer() {

    }

    public Developer(Long id, String title, String companyType, String location) {
        this.id = id;
        this.title = title;
        this.companyType = companyType;
        this.location = location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyType() {
        return companyType;
    }

    public String getLocation() {
        return location;
    }

    public List<Software> getSoftware() {
        return software;
    }
}