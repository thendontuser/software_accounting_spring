package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.License;

import java.time.LocalDate;

/**
 * Интерфейс доступа к данным таблицы license из БД
 * @author thendont
 * @version 1.0
 */
@Repository
public interface LicenseRepository extends CrudRepository<License, Long> {

    /**
     * Находит истекающие лицензии
     * @return Список истекающих лицензий
     */
    @Query(value = "SELECT * FROM license WHERE id IN (SELECT license_id FROM purchase) AND " +
            "dat_end - current_date <= 30",
            nativeQuery = true)
    Iterable<License> findExpiringLicenses();

    /**
     * Возвращает новую дату окончания лицензии
     * @param licenseId идентификатор лицензии
     * @param months количество месяцев, на которые продляется лицензия
     * @return Дата окончания лицензии
     */
    @Query(value = "SELECT (dat_end + make_interval(months => :months))::date FROM license WHERE id = :licenseId",
            nativeQuery = true)
    LocalDate extendEnd(Long licenseId, int months);
}