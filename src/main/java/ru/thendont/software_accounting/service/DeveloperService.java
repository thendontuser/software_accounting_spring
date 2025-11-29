package ru.thendont.software_accounting.service;

import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Developer;
import ru.thendont.software_accounting.repository.DeveloperRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;

    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    public List<Developer> findAll() {
        return (List<Developer>) developerRepository.findAll();
    }

    public Optional<Developer> findById(Long id) {
        return developerRepository.findById(id);
    }

    public Developer save(Developer developer) {
        return developerRepository.save(developer);
    }

    public void deleteById(Long id) {
        developerRepository.deleteById(id);
    }
}