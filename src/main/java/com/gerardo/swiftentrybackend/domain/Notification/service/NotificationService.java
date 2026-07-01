package com.gerardo.swiftentrybackend.domain.Notification.service;

import com.gerardo.swiftentrybackend.domain.Notification.dto.response.NotificationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Notification.enums.NotificationType;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;

import java.util.List;

// Operaciones de creación y consulta de notificaciones de usuario
public interface NotificationService {

    // Crea y persiste una notificación no leída para el usuario dado
    void createNotification(UserModel user, NotificationType type, String title,
                            String message, Long relatedEntityId);

    List<NotificationResponseDTO> getMyNotifications(String userEmail);

    List<NotificationResponseDTO> getMyUnread(String userEmail);

    long getMyUnreadCount(String userEmail);

    // Marca una notificación como leída si pertenece al usuario indicado
    NotificationResponseDTO markAsRead(Integer id, String userEmail);

    // Marca como leídas todas las notificaciones no leídas del usuario indicado
    void markAllAsRead(String userEmail);
}
