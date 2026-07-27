package com.example.CareerConnect.repository;

import com.example.CareerConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByResetToken(String resetToken);
}
