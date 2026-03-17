package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.InstallationTask;
import ru.thendont.software_accounting.entity.User;

import java.time.LocalDate;

@Repository
public interface InstallationTaskRepository extends CrudRepository<InstallationTask, Long> {

    Iterable<InstallationTask> findByAssignedBy(User assignedBy);

    Iterable<InstallationTask> findByAssignedTo(User assignedTo);

    @Query(value = "SELECT * FROM installation_task WHERE assigned_by in (SELECT id FROM users WHERE kaf_id = :kafId)" +
            "AND created_at BETWEEN :dateFrom AND :dateTo",
            nativeQuery = true)
    Iterable<InstallationTask> findByKafedraAndDateBetween(Long kafId, LocalDate dateFrom, LocalDate dateTo);
}