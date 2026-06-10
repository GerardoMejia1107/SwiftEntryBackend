package com.gerardo.swiftentrybackend.domain.User.repositories;

import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    boolean existsByEmail(String email);
    Optional<UserModel> findByEmail(String email);
}
