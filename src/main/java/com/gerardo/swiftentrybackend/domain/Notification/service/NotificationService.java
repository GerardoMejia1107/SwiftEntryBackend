package com.gerardo.swiftentrybackend.domain.Notification.service;

import com.gerardo.swiftentrybackend.domain.Notification.dto.response.NotificationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Notification.enums.NotificationType;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;

import java.util.List;

public interface NotificationService {

    void createNotification(UserModel user, NotificationType type, String title,
                            String message, Long relatedEntityId);

    List<NotificationResponseDTO> getMyNotifications(String userEmail);

    List<NotificationResponseDTO> getMyUnread(String userEmail);

    long getMyUnreadCount(String userEmail);

    NotificationResponseDTO markAsRead(Integer id, String userEmail);

    void markAllAsRead(String userEmail);
}
