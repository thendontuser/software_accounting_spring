package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Kafedra;
import ru.thendont.software_accounting.repository.KafedraRepository;

import java.util.List;
import java.util.Optional;

@Service
public class KafedraService {

    @Autowired
    private KafedraRepository kafedraRepository;

    public Optional<Kafedra> findById(Long id) {
        return kafedraRepository.findById(id);
    }

    public List<Kafedra> findAll() {
        return (List<Kafedra>) kafedraRepository.findAll();
    }

    public Kafedra save(Kafedra kafedra) {
        return kafedraRepository.save(kafedra);
    }

    public void deleteById(Long id) {
        kafedraRepository.deleteById(id);
    }
}