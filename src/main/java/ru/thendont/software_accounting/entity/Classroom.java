package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classroom")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cr_number")
    private String number;

    @ManyToOne
    @JoinColumn(name = "kaf_id")
    private Kafedra kafedra;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Device> devices = new ArrayList<>();

    public Classroom() {

    }

    public Classroom(Long id, String number, Kafedra kafedra) {
        this.id = id;
        this.number = number;
        this.kafedra = kafedra;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setKafedra(Kafedra kafedra) {
        this.kafedra = kafedra;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Kafedra getKafedra() {
        return kafedra;
    }

    public List<Device> getDevices() {
        return devices;
    }
}