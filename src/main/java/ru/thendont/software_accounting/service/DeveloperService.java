package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Developer;
import ru.thendont.software_accounting.repository.DeveloperRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DeveloperService {
    
    @Autowired
    private DeveloperRepository developerRepository;

    public Optional<Developer> findById(Long id) {
        return developerRepository.findById(id);
    }

    public List<Developer> findAll() {
        return (List<Developer>) developerRepository.findAll();
    }

    public Developer save(Developer developer) {
        return developerRepository.save(developer);
    }

    public void deleteById(Long id) {
        developerRepository.deleteById(id);
    }
}