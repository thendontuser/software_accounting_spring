package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "software")
public class Software {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "version")
    private String version;

    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @Column(name = "logo_path")
    private String logoPath;

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<License> licenses = new ArrayList<>();

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<InstallationRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<SoftwareInstallation> installations = new ArrayList<>();

    public Software() {

    }

    public Software(Long id, String title, String version, Developer developer, String logoPath) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.developer = developer;
        this.logoPath = logoPath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public List<InstallationRequest> getRequests() {
        return requests;
    }

    public List<SoftwareInstallation> getInstallations() {
        return installations;
    }

    public List<License> getLicenses() {
        return licenses;
    }
}