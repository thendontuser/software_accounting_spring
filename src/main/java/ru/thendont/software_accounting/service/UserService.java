package ru.thendont.software_accounting.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.repository.UserRepository;

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

    public static Optional<User> isAuthorise(User user, UserRepository userRepository) {
        User userFromDB = userRepository.findByLogin(user.getLogin()).orElse(null);
        if (userFromDB != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(user.getPassword(), userFromDB.getPassword())) {
                return Optional.of(userFromDB);
            }
        }
        return Optional.empty();
    }

    public static String getPageFromRole(String role) {
        switch (role) {
            case "ADMIN":
                return "admin-page";
            case "IT":
                return "it-page";
            case "ACCOUNTANT":
                return "accountant-page";
            case "MANAGER":
                return "manager-page";
            case "TEACHER":
                return "redirect:/teacher/dashboard";
            default:
                return "";
        }
    }
}