package ru.thendont.software_accounting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.thendont.software_accounting.entity.License;
import ru.thendont.software_accounting.entity.Software;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LicenseServiceTest {

    @Autowired
    private BaseCrudService<License> licenseBaseCrudService;

    @Autowired
    private BaseCrudService<Software> softwareBaseCrudService;

    @Autowired
    private SoftwareInstallationService softwareInstallationService;

    @Autowired
    private LicenseService licenseService;

    @Test
    public void testCreateLicense() {
        Software software = softwareBaseCrudService.findById(Long.valueOf(1)).orElse(null);
        License license = new License(null, "test", software, LocalDate.now(), LocalDate.now(), 5000);
        License saved = licenseBaseCrudService.save(license);

        assertNotNull(saved.getId());
        assertEquals(license.getPrice().intValue(), saved.getPrice().intValue());

        licenseBaseCrudService.deleteById(saved.getId());
    }

    @Test
    public void testUpdateLicense() {
        Software software = softwareBaseCrudService.findById(Long.valueOf(1)).orElse(null);
        License license = new License(null, "test", software, LocalDate.now(), LocalDate.now(), 5000);
        License saved = licenseBaseCrudService.save(license);
        License target = licenseBaseCrudService.findById(saved.getId()).orElse(null);

        target.setType("testUpdate");
        target = licenseBaseCrudService.save(target);

        assertNotEquals(license.getType(), target.getType());

        licenseBaseCrudService.deleteById(saved.getId());
    }

    @Test
    public void testDeleteLicense() {
        Software software = softwareBaseCrudService.findById(Long.valueOf(1)).orElse(null);
        License license = new License(null, "test", software, LocalDate.now(), LocalDate.now(), 5000);
        License saved = licenseBaseCrudService.save(license);

        licenseBaseCrudService.deleteById(saved.getId());

        assertNull(licenseBaseCrudService.findById(saved.getId()).orElse(null));
    }

    @Test
    public void testTotalPriceLicense() {
        List<Software> installedSoftware = softwareInstallationService.findAllInstalledSoftware();
        int totalPrice = 0;

        for (License license : licenseBaseCrudService.findAll()) {
            for (Software software : installedSoftware) {
                if (license.getSoftware().getId().equals(software.getId())) {
                    totalPrice += license.getPrice();
                }
            }
        }
        assertEquals(totalPrice, licenseService.getTotalCost().intValue());
    }
}