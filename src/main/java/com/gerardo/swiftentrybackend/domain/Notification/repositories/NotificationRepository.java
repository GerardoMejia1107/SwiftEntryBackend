package com.gerardo.swiftentrybackend.domain.Notification.repositories;

import com.gerardo.swiftentrybackend.domain.Notification.NotificationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acceso a datos de las notificaciones de usuario
public interface NotificationRepository extends JpaRepository<NotificationModel, Integer> {

    // Notificaciones de un usuario, más recientes primero
    List<NotificationModel> findByUser_IdOrderByCreatedAtDesc(Integer userId);

    // Notificaciones no leídas de un usuario, más recientes primero
    List<NotificationModel> findByUser_IdAndReadFalseOrderByCreatedAtDesc(Integer userId);

    // Cuenta las notificaciones no leídas de un usuario
    long countByUser_IdAndReadFalse(Integer userId);
}
