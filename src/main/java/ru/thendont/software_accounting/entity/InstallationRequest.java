package ru.thendont.software_accounting.entity;

import jakarta.persistence.*;
import ru.thendont.software_accounting.service.enums.InstallationRequestStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "installation_request")
public class InstallationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "software_id")
    private Software software;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "request_date")
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private InstallationRequestStatus status;

    @Column(name = "comment")
    private String comment;

    @OneToMany(mappedBy = "installationRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<InstallationTask> tasks = new ArrayList<>();

    public InstallationRequest() {

    }

    public InstallationRequest(Long id,
                               Software software,
                               Classroom classroom,
                               User user,
                               LocalDate date,
                               InstallationRequestStatus status,
                               String comment) {
        this.id = id;
        this.software = software;
        this.classroom = classroom;
        this.user = user;
        this.date = date;
        this.status = status;
        this.comment = comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(InstallationRequestStatus status) {
        this.status = status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Software getSoftware() {
        return software;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDate() {
        return date;
    }

    public InstallationRequestStatus getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public List<InstallationTask> getTasks() {
        return tasks;
    }
}