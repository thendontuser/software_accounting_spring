package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Software;
import ru.thendont.software_accounting.repository.SoftwareRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SoftwareService {

    @Autowired
    private SoftwareRepository softwareRepository;

    public Optional<Software> findById(Long id) {
        return softwareRepository.findById(id);
    }

    public List<Software> findAll() {
        return (List<Software>) softwareRepository.findAll();
    }

    public Software save(Software software) {
        return softwareRepository.save(software);
    }

    public void deleteById(Long id) {
        softwareRepository.deleteById(id);
    }
}