package com.example.jobportal.config;

import com.example.jobportal.entity.User;
import com.example.jobportal.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Initializing users for database job_portal_db ===");

        // Fix existing users with plain text passwords
        fixExistingUsers();

        // Create default users
        ensureTestUsersExist();

        System.out.println("=== User initialization complete ===");
    }

    private void fixExistingUsers() {
        System.out.println("Checking existing users for password encoding...");

        userService.findAllUsers().forEach(user -> {
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                System.out.println("Fixing password for user: " + user.getUsername());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.updateUser(user);
                System.out.println("✓ Password updated for: " + user.getUsername());
            } else {
                System.out.println("✓ Password already encoded for: " + user.getUsername());
            }
        });
    }

    private void ensureTestUsersExist() {

        createUser(
                "admin",
                "admin123",
                "Administrator",
                "admin@jobportal.com",
                "ROLE_ADMIN"
        );

        createUser(
                "student",
                "student123",
                "Test Student",
                "student@jobportal.com",
                "ROLE_STUDENT"
        );

        createUser(
                "harsha",
                "Harsha@123",
                "Harsha",
                "blgowda4808harsha@gmail.com",
                "ROLE_STUDENT"
        );

        createUser(
                "vivechan",
                "Vivi@123",
                "Vivechan",
                "itsmegowda05@gmail.com",
                "ROLE_STUDENT"
        );

        createUser(
                "employer",
                "employer123",
                "Test Employer",
                "employer@jobportal.com",
                "ROLE_EMPLOYER"
        );
    }

    private void createUser(String username,
                            String password,
                            String name,
                            String email,
                            String role) {

        if (userService.findByUsername(username) == null) {

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setEmail(email);
            user.setRole(role);
            user.setEnabled(true);

            userService.saveUser(user);

            System.out.println("✓ " + username + " user created!");

        } else {
            System.out.println("✓ " + username + " already exists");
        }
    }
}