package com.example.CareerConnect.config;

import com.example.CareerConnect.entity.User;
import com.example.CareerConnect.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Demo account passwords
    @Value("${initial.guest-password}")
    private String guestPassword;

    @Value("${initial.demo-recruiter-password}")
    private String demoRecruiterPassword;

    @Value("${initial.demo-admin-password}")
    private String demoAdminPassword;

    public DataInitializer(
            UserService userService,
            PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("=== Initializing CareerConnect users ===");

        // Make sure existing plaintext passwords are encoded
        fixExistingUsers();

        // Create demo accounts if they don't already exist
        ensureDemoUsersExist();

        System.out.println("=== User initialization complete ===");
    }

    /**
     * Checks existing users.
     *
     * If a password is not BCrypt encoded, encode it.
     * Already encoded passwords are NOT changed.
     */
    private void fixExistingUsers() {

        System.out.println("Checking existing users for password encoding...");

        userService.findAllUsers().forEach(user -> {

            String password = user.getPassword();

            if (password != null
                    && !password.startsWith("$2a$")
                    && !password.startsWith("$2b$")
                    && !password.startsWith("$2y$")) {

                System.out.println(
                        "Fixing password for user: "
                                + user.getUsername()
                );

                user.setPassword(
                        passwordEncoder.encode(password)
                );

                userService.updateUser(user);

                System.out.println(
                        "✓ Password updated for: "
                                + user.getUsername()
                );

            } else {

                System.out.println(
                        "✓ Password already encoded for: "
                                + user.getUsername()
                );
            }
        });
    }

    /**
     * Creates only the public demo accounts.
     *
     * Existing users such as admin, student, harsha,
     * vivechan and employer are NOT modified.
     */
    private void ensureDemoUsersExist() {

        // Student Demo Account
        createUser(
                "guest",
                guestPassword,
                "Guest Student",
                "guest@careerconnect.com",
                "ROLE_STUDENT"
        );

        // Recruiter Demo Account
        createUser(
                "guest_recruiter",
                demoRecruiterPassword,
                "Demo Recruiter",
                "guest.recruiter@careerconnect.com",
                "ROLE_EMPLOYER"
        );

        // Demo Admin Account
        createUser(
                "guest_admin",
                demoAdminPassword,
                "Demo Admin",
                "guest.admin@careerconnect.com",
                "ROLE_DEMO_ADMIN"
        );
    }

    /**
     * Creates a user only if the username does not already exist.
     */
    private void createUser(
            String username,
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

            // UserService handles BCrypt encoding
            userService.saveUser(user);

            System.out.println(
                    "✓ " + username + " demo user created!"
            );

        } else {

            System.out.println(
                    "✓ " + username + " already exists"
            );
        }
    }
}