package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.InstallationReport;

@Repository
public interface InstallationReportRepository extends CrudRepository<InstallationReport, Long> {

    @Query(value = "SELECT * FROM installation_report WHERE installation_task_id IN " +
            "(SELECT id FROM installation_task WHERE assigned_by = :assignedBy)",
            nativeQuery = true)
    Iterable<InstallationReport> findByTaskAssignedBy(Long assignedBy);

    @Query(value = "SELECT * FROM installation_report WHERE installation_task_id IN " +
            "(SELECT id FROM installation_task WHERE assigned_to = :assignedTo)",
            nativeQuery = true)
    Iterable<InstallationReport> findByTaskAssignedTo(Long assignedTo);
}