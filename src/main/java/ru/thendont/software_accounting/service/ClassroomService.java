package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Classroom;
import ru.thendont.software_accounting.entity.Kafedra;
import ru.thendont.software_accounting.repository.ClassroomRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;

    public Optional<Classroom> findById(Long id) {
        return classroomRepository.findById(id);
    }

    public List<Classroom> findAll() {
        return (List<Classroom>) classroomRepository.findAll();
    }

    public Classroom save(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    public void deleteById(Long id) {
        classroomRepository.deleteById(id);
    }

    public List<Classroom> findByKafedra(Kafedra kafedra) {
        return (List<Classroom>) classroomRepository.findByKafId(kafedra.getId());
    }
}