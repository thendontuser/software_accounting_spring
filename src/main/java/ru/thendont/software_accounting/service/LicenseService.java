package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.License;
import ru.thendont.software_accounting.repository.LicenseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Класс-сервис для управления бизнес логикой, связанной с таблицей license
 * @author thendont
 * @version 1.0
 */
@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    public Optional<License> findById(Long id) {
        return licenseRepository.findById(id);
    }

    public List<License> findAll() {
        return (List<License>) licenseRepository.findAll();
    }

    public License save(License license) {
        return licenseRepository.save(license);
    }

    public void deleteById(Long id) {
        licenseRepository.deleteById(id);
    }

    /**
     * Находит истекающие лицензии
     * @return Список истекающих лицензий
     */
    public List<License> findExpiringLicenses() {
        return (List<License>) licenseRepository.findExpiringLicenses();
    }

    /**
     * Возвращает новую дату окончания лицензии
     * @param license лицензия
     * @param months количество месяцев, на которые продляется лицензия
     * @return Дата окончания лицензии
     */
    public LocalDate extendEnd(License license, int months) {
        return licenseRepository.extendEnd(license.getId(), months);
    }
}