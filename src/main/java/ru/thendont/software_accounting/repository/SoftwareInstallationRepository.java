package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.SoftwareInstallation;

/**
 * Интерфейс доступа к данным таблицы software_installation из БД
 * @author thendont
 * @version 1.0
 */
@Repository
public interface SoftwareInstallationRepository extends CrudRepository<SoftwareInstallation, Long> {

    /**
     * Находит установки ПО по кафедре
     * @param kafId идентификатор кафедры
     * @return Список установок ПО по кафедре
     */
    @Query(value = "SELECT * FROM software_installation WHERE device_id IN (SELECT id FROM device WHERE classroom_id IN" +
            "(SELECT id FROM classroom WHERE kaf_id = :kafId))",
            nativeQuery = true)
    Iterable<SoftwareInstallation> findByKafedra(Long kafId);
}