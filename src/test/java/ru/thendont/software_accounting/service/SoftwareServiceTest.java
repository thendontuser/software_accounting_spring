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
    private SoftwareService softwareService;

    @Autowired
    private DeveloperService developerService;

    @Test
    public void testCreateSoftware() {
        Developer developer = developerService.findById(Long.valueOf(3)).orElse(null);
        Software software = new Software(null, "testTitle", "testVersion", developer, "testLogo");
        Software saved = softwareService.save(software);

        assertNotNull(saved.getId());
        assertEquals("testTitle", saved.getTitle());

        softwareService.deleteById(saved.getId());
    }

    @Test
    public void testUpdateSoftware() {
        Developer developer = developerService.findById(Long.valueOf(3)).orElse(null);
        Software software = new Software(null, "testTitle", "testVersion", developer, "testLogo");
        Software saved = softwareService.save(software);
        Software target = softwareService.findById(Long.valueOf(saved.getId())).orElse(null);

        target.setVersion("1.5");
        target = softwareService.save(target);

        assertNotEquals(software.getVersion(), target.getVersion());

        softwareService.deleteById(saved.getId());
    }

    @Test
    public void testDeleteSoftware() {
        Developer developer = developerService.findById(Long.valueOf(3)).orElse(null);
        Software software = new Software(null, "testTitle", "testVersion", developer, "testLogo");
        Software saved = softwareService.save(software);

        softwareService.deleteById(saved.getId());

        assertNull(softwareService.findById(saved.getId()).orElse(null));
    }
}