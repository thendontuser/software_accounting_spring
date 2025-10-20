package ru.thendont.software_accounting.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    public static void hashPassword(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordEncoded);
    }

    public static void processDepartment(User user) {
        if (!user.getRole().equals("MANAGER") && !user.getRole().equals("TEACHER")) {
            user.setDepartment(null);
        }
    }

    public static Optional<User> isAuthorise(User user, List<User> users, UserRepository userRepository) {
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