package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.repository.BaseCrudRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BaseCrudService<E> {

    @Autowired
    private BaseCrudRepository<E> baseCrudRepository;

    public Optional<E> findById(Long id) {
        return baseCrudRepository.findById(id);
    }

    public List<E> findAll() {
        return (List<E>) baseCrudRepository.findAll();
    }

    public E save(E entity) {
        return baseCrudRepository.save(entity);
    }

    public void deleteById(Long id) {
        baseCrudRepository.deleteById(id);
    }
}