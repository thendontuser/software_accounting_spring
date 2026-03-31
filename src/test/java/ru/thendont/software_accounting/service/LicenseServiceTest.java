package ru.thendont.software_accounting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.thendont.software_accounting.entity.License;
import ru.thendont.software_accounting.entity.Software;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Предоставляет методы для модульного тестирования сервиса лицензий
 * @author thendont
 * @version 1.0
 */
@SpringBootTest
public class LicenseServiceTest {

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private SoftwareService softwareService;

    @Autowired
    private SoftwareInstallationService softwareInstallationService;

    /**
     * Тестирует создание новой записи лицензии
     */
    @Test
    public void testCreateLicense() {
        Software software = softwareService.findById(1L).orElse(null);
        License license = new License(null, "test", software, LocalDate.now(), LocalDate.now(), 5000);
        License saved = licenseService.save(license);

        assertNotNull(saved.getId());
        assertEquals(license.getPrice().intValue(), saved.getPrice().intValue());

        licenseService.deleteById(saved.getId());
    }

    /**
     * Тестирует обновление записи лицензии
     */
    @Test
    public void testUpdateLicense() {
        Software software = softwareService.findById(1L).orElse(null);
        License license = new License(null, "test", software, LocalDate.now(), LocalDate.now(), 5000);
        License saved = licenseService.save(license);
        License target = licenseService.findById(saved.getId()).orElse(null);

        assert target != null;

        target.setType("testUpdate");
        target = licenseService.save(target);

        assertNotEquals(license.getType(), target.getType());

        licenseService.deleteById(saved.getId());
    }

    /**
     * Тестирует удаление записи лицензии
     */
    @Test
    public void testDeleteLicense() {
        Software software = softwareService.findById(1L).orElse(null);
        License license = new License(null, "test", software, LocalDate.now(), LocalDate.now(), 5000);
        License saved = licenseService.save(license);

        licenseService.deleteById(saved.getId());

        assertNull(licenseService.findById(saved.getId()).orElse(null));
    }
}