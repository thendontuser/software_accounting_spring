package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "license")
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "l_type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "software_id")
    private Software software;

    @Column(name = "dat_beg")
    private LocalDate dateBegin;

    @Column(name = "dat_end")
    private LocalDate dateEnd;

    @Column(name = "price")
    private Integer price;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Purchase> purchases = new ArrayList<>();

    public License() {

    }

    public License(Long id, String type, Software software, LocalDate dateBegin, LocalDate dateEnd, Integer price) {
        this.id = id;
        this.type = type;
        this.software = software;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.price = price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public void setDateBegin(LocalDate dateBegin) {
        this.dateBegin = dateBegin;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Software getSoftware() {
        return software;
    }

    public LocalDate getDateBegin() {
        return dateBegin;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public Integer getPrice() {
        return price;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }
}