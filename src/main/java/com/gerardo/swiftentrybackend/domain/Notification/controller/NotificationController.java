package com.gerardo.swiftentrybackend.domain.Notification.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Notification.dto.response.NotificationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/swift_entry/notifications")
@RequiredArgsConstructor
// Expone los endpoints de notificaciones del usuario autenticado
public class NotificationController {

    private final NotificationService notificationService;
    private final ResponseBuilder responseBuilder;

    // Lista todas las notificaciones del usuario autenticado, más recientes primero
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyNotifications(Authentication authentication) {
        List<NotificationResponseDTO> response =
                notificationService.getMyNotifications(authentication.getName());
        return responseBuilder.buildResponse("Notifications retrieved", HttpStatus.OK, response);
    }

    // Lista únicamente las notificaciones no leídas del usuario autenticado
    @GetMapping("/me/unread")
    public ResponseEntity<GeneralResponse> getMyUnread(Authentication authentication) {
        List<NotificationResponseDTO> response =
                notificationService.getMyUnread(authentication.getName());
        return responseBuilder.buildResponse("Unread notifications retrieved", HttpStatus.OK, response);
    }

    // Devuelve el conteo de notificaciones no leídas del usuario autenticado
    @GetMapping("/me/unread-count")
    public ResponseEntity<GeneralResponse> getMyUnreadCount(Authentication authentication) {
        long count = notificationService.getMyUnreadCount(authentication.getName());
        return responseBuilder.buildResponse(
                "Unread notification count retrieved", HttpStatus.OK, Map.of("unreadCount", count));
    }

    // Marca una notificación puntual como leída, validando que pertenezca al usuario
    @PatchMapping("/{id}/read")
    public ResponseEntity<GeneralResponse> markAsRead(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        NotificationResponseDTO response =
                notificationService.markAsRead(id, authentication.getName());
        return responseBuilder.buildResponse("Notification marked as read", HttpStatus.OK, response);
    }

    // Marca todas las notificaciones no leídas del usuario autenticado como leídas
    @PatchMapping("/me/read-all")
    public ResponseEntity<GeneralResponse> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return responseBuilder.buildResponse("All notifications marked as read", HttpStatus.OK, null);
    }
}
