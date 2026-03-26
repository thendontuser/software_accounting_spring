package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.entity.Kafedra;
import ru.thendont.software_accounting.repository.InstallationRequestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Класс-сервис для управления бизнес логикой, связанной с таблицей installation_request
 * @author thendont
 * @version 1.0
 */
@Service
public class InstallationRequestService {

    @Autowired
    private InstallationRequestRepository installationRequestRepository;

    public Optional<InstallationRequest> findById(Long id) {
        return installationRequestRepository.findById(id);
    }

    public List<InstallationRequest> findAll() {
        return (List<InstallationRequest>) installationRequestRepository.findAll();
    }

    public InstallationRequest save(InstallationRequest installationRequest) {
        return installationRequestRepository.save(installationRequest);
    }

    public void deleteById(Long id) {
        installationRequestRepository.deleteById(id);
    }

    public List<InstallationRequest> findByKafedra(Kafedra kafedra) {
        return (List<InstallationRequest>) installationRequestRepository.findByKafedraId(kafedra.getId());
    }

    /**
     * Находит заявки пользователей по кафедре и по промежутку дат
     * @param kafedra кафедра
     * @param dateFrom дата начала выборки
     * @param dateTo дата окончания выборки
     * @return Список заявок пользователей по кафедре и по промежутку дат
     */
    public List<InstallationRequest> findByKafedraAndDateBetween(Kafedra kafedra, LocalDate dateFrom, LocalDate dateTo) {
        return (List<InstallationRequest>) installationRequestRepository.findByKafedraAndDateBetween(
                kafedra.getId(), dateFrom, dateTo
        );
    }
}