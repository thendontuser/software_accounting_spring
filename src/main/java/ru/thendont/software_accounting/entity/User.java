package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;
import ru.thendont.software_accounting.service.enums.UserRoles;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "email")
    private String email;

    @Column(name = "login")
    private String username;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "role")
    private UserRoles role;

    @ManyToOne
    @JoinColumn(name = "dep_number")
    private Department department;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<InstallationRequest> requests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<SoftwareInstallation> installations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<LicenseRequest> licenseRequests = new ArrayList<>();

    public User() {

    }

    public User(Long id,
                String lastName,
                String firstName,
                String patronymic,
                String email,
                String username,
                String password,
                UserRoles role,
                Department department) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRoles getRole() {
        return role;
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

    public List<LicenseRequest> getLicenseRequests() {
        return licenseRequests;
    }
}