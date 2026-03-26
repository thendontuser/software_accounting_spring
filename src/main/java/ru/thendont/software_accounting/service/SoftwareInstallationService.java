package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Kafedra;
import ru.thendont.software_accounting.entity.SoftwareInstallation;
import ru.thendont.software_accounting.repository.SoftwareInstallationRepository;

import java.util.List;
import java.util.Optional;

/**
 * Класс-сервис для управления бизнес логикой, связанной с таблицей software_installation
 * @author thendont
 * @version 1.0
 */
@Service
public class SoftwareInstallationService {

    @Autowired
    private SoftwareInstallationRepository softwareInstallationRepository;

    public Optional<SoftwareInstallation> findById(Long id) {
        return softwareInstallationRepository.findById(id);
    }

    public List<SoftwareInstallation> findAll() {
        return (List<SoftwareInstallation>) softwareInstallationRepository.findAll();
    }

    public SoftwareInstallation save(SoftwareInstallation softwareInstallation) {
        return softwareInstallationRepository.save(softwareInstallation);
    }

    public void deleteById(Long id) {
        softwareInstallationRepository.deleteById(id);
    }

    /**
     * Находит установки ПО по кафедре
     * @param kafedra кафедра
     * @return Список установок ПО по кафедре
     */
    public List<SoftwareInstallation> findByKafedra(Kafedra kafedra) {
        return (List<SoftwareInstallation>) softwareInstallationRepository.findByKafedra(kafedra.getId());
    }
}