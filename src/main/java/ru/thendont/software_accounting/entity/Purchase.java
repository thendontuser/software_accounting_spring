package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "license_request_id")
    private LicenseRequest licenseRequest;

    @OneToOne
    @JoinColumn(name = "bought_by")
    private User boughtBy;

    @Column(name = "bought_at")
    private LocalDate boughtAt;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "cost")
    private Integer cost;

    @ManyToOne
    @JoinColumn(name = "license_id")
    private License license;

    public Purchase() {

    }

    public Purchase(Long id,
                    LicenseRequest licenseRequest,
                    User boughtBy,
                    LocalDate boughtAt,
                    String contractNumber,
                    Integer cost,
                    License license) {
        this.id = id;
        this.licenseRequest = licenseRequest;
        this.boughtBy = boughtBy;
        this.boughtAt = boughtAt;
        this.contractNumber = contractNumber;
        this.cost = cost;
        this.license = license;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLicenseRequest(LicenseRequest licenseRequest) {
        this.licenseRequest = licenseRequest;
    }

    public void setBoughtBy(User boughtBy) {
        this.boughtBy = boughtBy;
    }

    public void setBoughtAt(LocalDate boughtAt) {
        this.boughtAt = boughtAt;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Long getId() {
        return id;
    }

    public LicenseRequest getLicenseRequest() {
        return licenseRequest;
    }

    public User getBoughtBy() {
        return boughtBy;
    }

    public LocalDate getBoughtAt() {
        return boughtAt;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public Integer getCost() {
        return cost;
    }

    public License getLicense() {
        return license;
    }
}