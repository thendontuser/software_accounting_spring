package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "kafedra")
public class Kafedra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "dep_number")
    private Department department;

    public Kafedra() {

    }

    public Kafedra(Long id, String title, Department department) {
        this.id = id;
        this.title = title;
        this.department = department;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Department getDepartment() {
        return department;
    }
}