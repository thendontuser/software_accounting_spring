package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.Classroom;

@Repository
public interface ClassroomRepository extends CrudRepository<Classroom, Long> {

    @Query(value = "SELECT * FROM classroom WHERE kaf_id = :kafId", nativeQuery = true)
    Iterable<Classroom> findByKafId(Long kafId);
}