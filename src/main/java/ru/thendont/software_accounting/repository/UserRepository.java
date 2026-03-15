package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.Kafedra;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.enums.UserRoles;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Iterable<User> findByRole(UserRoles role);

    Iterable<User> findByRoleAndKafedra(UserRoles role, Kafedra kafedra);

    @Query(value = "SELECT * FROM users WHERE role IS NULL AND kaf_id IS NULL", nativeQuery = true)
    Iterable<User> findPendingUsers();
}