package com.gerardo.swiftentrybackend.security.auth.repositories;

import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.security.auth.models.RefreshTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// Acceso a datos de RefreshTokenModel
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Integer> {

    // Busca un refresh token por su valor, cargando el usuario asociado en la misma query
    @Query("SELECT rt FROM RefreshTokenModel rt JOIN FETCH rt.user WHERE rt.token = :token")
    Optional<RefreshTokenModel> findByTokenWithUser(@Param("token") String token);

    // Elimina un refresh token específico (logout / rotación)
    void deleteByToken(String token);

    // Elimina todos los refresh tokens de un usuario
    void deleteAllByUser(UserModel user);
}
