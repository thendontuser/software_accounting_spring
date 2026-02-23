package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.Classroom;

import java.util.List;

@Repository
public interface ClassroomRepository extends CrudRepository<Classroom, Long> {

    @Query(value = "SELECT * FROM classroom WHERE kaf_id IN " +
            "(SELECT id FROM kafedra WHERE dep_number = :depNumber)", nativeQuery = true)
    List<Classroom> findByDepartmentNumber(Long depNumber);
}