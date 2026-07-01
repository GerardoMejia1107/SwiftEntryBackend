package com.gerardo.swiftentrybackend.domain.Notification.dto.response;

import com.gerardo.swiftentrybackend.domain.Notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de una notificación devueltos al cliente")
// Datos de una notificación devueltos al cliente
public class NotificationResponseDTO {

    @Schema(description = "ID de la notificación", example = "1")
    private Integer id;

    @Schema(description = "ID del usuario destinatario de la notificación", example = "5")
    private Integer userId;

    @Schema(description = "Tipo de evento que generó la notificación")
    private NotificationType type;

    @Schema(description = "Título breve de la notificación", example = "A seat is now available")
    private String title;

    @Schema(description = "Mensaje detallado de la notificación", example = "A seat just became available for VIP. You have 30 minutes to complete your reservation before your spot is released.")
    private String message;

    @Schema(description = "ID de la entidad relacionada con la notificación (p. ej. el ID de la localidad cuando el tipo es WAITING_LIST_SEAT_AVAILABLE)", example = "3")
    private Long relatedEntityId;

    @Schema(description = "Indica si el usuario ya leyó la notificación", example = "false")
    private Boolean read;

    @Schema(description = "Fecha y hora en la que se marcó la notificación como leída; null si aún no ha sido leída")
    private LocalDateTime readAt;

    @Schema(description = "Fecha y hora en la que se creó la notificación")
    private LocalDateTime createdAt;
}
