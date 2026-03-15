package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.InstallationRequest;

import java.time.LocalDate;

@Repository
public interface InstallationRequestRepository extends CrudRepository<InstallationRequest, Long> {

    @Query(value = "SELECT * FROM installation_request WHERE classroom_id IN (SELECT id FROM classroom WHERE kaf_id = :kafId)",
            nativeQuery = true)
    Iterable<InstallationRequest> findByKafedraId(Long kafId);

    @Query(value = "SELECT * FROM installation_request WHERE classroom_id IN (SELECT id FROM classroom WHERE kaf_id = :kafId)" +
            "AND request_date BETWEEN :dateFrom AND :dateTo",
            nativeQuery = true)
    Iterable<InstallationRequest> findByKafedraAndDateBetween(Long kafId, LocalDate dateFrom, LocalDate dateTo);
}