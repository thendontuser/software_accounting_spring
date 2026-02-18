package ru.thendont.software_accounting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.thendont.software_accounting.entity.Developer;
import ru.thendont.software_accounting.entity.Software;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SoftwareServiceTest {

    @Autowired
    private BaseCrudService<Software> softwareBaseCrudService;

    @Autowired
    private BaseCrudService<Developer> developerBaseCrudService;

    @Test
    public void testCreateSoftware() {
        Developer developer = developerBaseCrudService.findById(Long.valueOf(3)).orElse(null);
        Software software = new Software(null, "testTitle", "testVersion", developer, "testLogo");
        Software saved = softwareBaseCrudService.save(software);

        assertNotNull(saved.getId());
        assertEquals("testTitle", saved.getTitle());

        softwareBaseCrudService.deleteById(saved.getId());
    }

    @Test
    public void testUpdateSoftware() {
        Developer developer = developerBaseCrudService.findById(Long.valueOf(3)).orElse(null);
        Software software = new Software(null, "testTitle", "testVersion", developer, "testLogo");
        Software saved = softwareBaseCrudService.save(software);
        Software target = softwareBaseCrudService.findById(Long.valueOf(saved.getId())).orElse(null);

        target.setVersion("1.5");
        target = softwareBaseCrudService.save(target);

        assertNotEquals(software.getVersion(), target.getVersion());

        softwareBaseCrudService.deleteById(saved.getId());
    }

    @Test
    public void testDeleteSoftware() {
        Developer developer = developerBaseCrudService.findById(Long.valueOf(3)).orElse(null);
        Software software = new Software(null, "testTitle", "testVersion", developer, "testLogo");
        Software saved = softwareBaseCrudService.save(software);

        softwareBaseCrudService.deleteById(saved.getId());

        assertNull(softwareBaseCrudService.findById(saved.getId()).orElse(null));
    }
}