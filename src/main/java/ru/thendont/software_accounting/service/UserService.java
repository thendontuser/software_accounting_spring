package ru.thendont.software_accounting.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.repository.UserRepository;
import ru.thendont.software_accounting.util.UserRoles;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void hashPassword(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordEncoded);
    }

    public void processDepartment(User user) {
        if (!user.getRole().equals(UserRoles.MANAGER) && !user.getRole().equals(UserRoles.TEACHER)) {
            user.setDepartment(null);
        }
    }

    public Optional<User> isAuthorise(User user) {
        User userFromDB = userRepository.findByLogin(user.getLogin()).orElse(null);
        if (userFromDB != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(user.getPassword(), userFromDB.getPassword())) {
                return Optional.of(userFromDB);
            }
        }
        return Optional.empty();
    }
}