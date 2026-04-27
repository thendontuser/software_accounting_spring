package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.Classroom;
import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.entity.Software;

import java.time.LocalDate;

/**
 * Интерфейс доступа к данным таблицы installation_request из БД
 * @author thendont
 * @version 1.2
 */
@Repository
public interface InstallationRequestRepository extends CrudRepository<InstallationRequest, Long> {

    /**
     * Находит заявки пользователей по кафедре
     * @param kafId идентификатор кафедры
     * @return Список заявок пользователей по кафедре
     */
    @Query(value = "SELECT * FROM installation_request WHERE classroom_id IN (SELECT id FROM classroom WHERE kaf_id = :kafId)",
            nativeQuery = true)
    Iterable<InstallationRequest> findByKafedraId(Long kafId);

    /**
     * Находит заявки пользователей по кафедре и по промежутку дат
     * @param kafId идентификатор кафедры
     * @param dateFrom дата начала выборки
     * @param dateTo дата окончания выборки
     * @return Список заявок пользователей по кафедре и по промежутку дат
     */
    @Query(value = "SELECT * FROM installation_request WHERE classroom_id IN (SELECT id FROM classroom WHERE kaf_id = :kafId)" +
            "AND request_date BETWEEN :dateFrom AND :dateTo",
            nativeQuery = true)
    Iterable<InstallationRequest> findByKafedraAndDateBetween(Long kafId, LocalDate dateFrom, LocalDate dateTo);

    Iterable<InstallationRequest> findBySoftwareAndClassroom(Software software, Classroom classroom);
}