package com.gerardo.swiftentrybackend.domain.User.repositories;

import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    boolean existsByEmail(String email);
}
