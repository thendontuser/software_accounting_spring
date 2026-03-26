package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс-сущность таблицы software из БД
 * @author thendont
 * @version 1.0
 */
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

    @OneToMany(mappedBy = "software")
    private final List<License> licenses = new ArrayList<>();

    @OneToMany(mappedBy = "software")
    private final List<InstallationRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "software")
    private final List<SoftwareInstallation> installations = new ArrayList<>();

    @OneToMany(mappedBy = "software")
    private final List<LicenseRequest> licenseRequests = new ArrayList<>();

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

    /**
     * Возвращает список заявок на установку ПО
     * @return список заявок на установку ПО
     */
    public List<InstallationRequest> getRequests() {
        return requests;
    }

    /**
     * Возвращает список установок ПО
     * @return Список установок ПО
     */
    public List<SoftwareInstallation> getInstallations() {
        return installations;
    }

    /**
     * Возвращает список лицензий ПО
     * @return Список лицензий ПО
     */
    public List<License> getLicenses() {
        return licenses;
    }

    /**
     * Возвращает список заявок на покупку лицензий ПО
     * @return Список заявок на покупку лицензий ПО
     */
    public List<LicenseRequest> getLicenseRequests() {
        return licenseRequests;
    }
}