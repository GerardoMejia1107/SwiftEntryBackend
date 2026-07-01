package com.gerardo.swiftentrybackend.domain.Notification.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Notification.dto.response.NotificationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Notificaciones", description = "Consulta y gestión de las notificaciones del usuario autenticado (p. ej. avisos de cupo disponible en lista de espera)")
@RestController
@RequestMapping("/swift_entry/notifications")
@RequiredArgsConstructor
// Expone los endpoints de notificaciones del usuario autenticado
public class NotificationController {

    private final NotificationService notificationService;
    private final ResponseBuilder responseBuilder;

    // Lista todas las notificaciones del usuario autenticado, más recientes primero
    @Operation(summary = "Mis notificaciones", description = "Lista todas las notificaciones del usuario autenticado, ordenadas de más reciente a más antigua")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = NotificationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyNotifications(Authentication authentication) {
        List<NotificationResponseDTO> response =
                notificationService.getMyNotifications(authentication.getName());
        return responseBuilder.buildResponse("Notifications retrieved", HttpStatus.OK, response);
    }

    // Lista únicamente las notificaciones no leídas del usuario autenticado
    @Operation(summary = "Mis notificaciones no leídas", description = "Lista únicamente las notificaciones no leídas del usuario autenticado, ordenadas de más reciente a más antigua")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones no leídas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = NotificationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me/unread")
    public ResponseEntity<GeneralResponse> getMyUnread(Authentication authentication) {
        List<NotificationResponseDTO> response =
                notificationService.getMyUnread(authentication.getName());
        return responseBuilder.buildResponse("Unread notifications retrieved", HttpStatus.OK, response);
    }

    // Devuelve el conteo de notificaciones no leídas del usuario autenticado
    @Operation(summary = "Conteo de no leídas", description = "Devuelve la cantidad de notificaciones no leídas del usuario autenticado, útil para un badge en el cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente. Devuelve un objeto con la clave 'unreadCount' (ej. {\"unreadCount\": 3})"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me/unread-count")
    public ResponseEntity<GeneralResponse> getMyUnreadCount(Authentication authentication) {
        long count = notificationService.getMyUnreadCount(authentication.getName());
        return responseBuilder.buildResponse(
                "Unread notification count retrieved", HttpStatus.OK, Map.of("unreadCount", count));
    }

    // Marca una notificación puntual como leída, validando que pertenezca al usuario
    @Operation(summary = "Marcar notificación como leída", description = "Marca una notificación puntual como leída. Solo el dueño de la notificación puede marcarla")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente",
                    content = @Content(schema = @Schema(implementation = NotificationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "La notificación no pertenece al usuario autenticado"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada, o usuario no encontrado")
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<GeneralResponse> markAsRead(
            @Parameter(description = "ID de la notificación", example = "1")
            @PathVariable Integer id,
            Authentication authentication
    ) {
        NotificationResponseDTO response =
                notificationService.markAsRead(id, authentication.getName());
        return responseBuilder.buildResponse("Notification marked as read", HttpStatus.OK, response);
    }

    // Marca todas las notificaciones no leídas del usuario autenticado como leídas
    @Operation(summary = "Marcar todas como leídas", description = "Marca en bloque todas las notificaciones no leídas del usuario autenticado como leídas. No hace nada si no hay ninguna pendiente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones marcadas como leídas exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/me/read-all")
    public ResponseEntity<GeneralResponse> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return responseBuilder.buildResponse("All notifications marked as read", HttpStatus.OK, null);
    }
}
