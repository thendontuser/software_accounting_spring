package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.InstallationReport;

/**
 * Интерфейс доступа к данным таблицы installation_report из БД
 * @author thendont
 * @version 1.0
 */
@Repository
public interface InstallationReportRepository extends CrudRepository<InstallationReport, Long> {

    /**
     * Находит записи отчетов по идентификатору пользователя, поручившего задачу
     * @param assignedBy идентификатор пользователя, поручившего задачу
     * @return Список отчетов
     */
    @Query(value = "SELECT * FROM installation_report WHERE installation_task_id IN " +
            "(SELECT id FROM installation_task WHERE assigned_by = :assignedBy)",
            nativeQuery = true)
    Iterable<InstallationReport> findByTaskAssignedBy(Long assignedBy);

    /**
     * Находит записи отчетов по идентификатору пользователя, которому поручена задача
     * @param assignedTo идентификатор пользователя, которому поручена задача
     * @return Список отчетов
     */
    @Query(value = "SELECT * FROM installation_report WHERE installation_task_id IN " +
            "(SELECT id FROM installation_task WHERE assigned_to = :assignedTo)",
            nativeQuery = true)
    Iterable<InstallationReport> findByTaskAssignedTo(Long assignedTo);
}