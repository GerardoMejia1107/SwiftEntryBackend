package com.gerardo.swiftentrybackend.security.auth.repositories;

import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.security.auth.models.RefreshTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Integer> {

    @Query("SELECT rt FROM RefreshTokenModel rt JOIN FETCH rt.user WHERE rt.token = :token")
    Optional<RefreshTokenModel> findByTokenWithUser(@Param("token") String token);

    void deleteByToken(String token);

    void deleteAllByUser(UserModel user);
}
