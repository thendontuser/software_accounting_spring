package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    public Optional<User> findByLogin(String login);
}