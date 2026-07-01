package com.gerardo.swiftentrybackend.domain.Notification.dto.response;

import com.gerardo.swiftentrybackend.domain.Notification.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos de una notificación devueltos al cliente
public class NotificationResponseDTO {

    private Integer id;
    private Integer userId;
    private NotificationType type;
    private String title;
    private String message;
    private Long relatedEntityId;
    private Boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
