package com.gerardo.swiftentrybackend.domain.WaitingList.dto.response;

import com.gerardo.swiftentrybackend.domain.WaitingList.enums.WaitingListStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de una entrada de lista de espera devueltos al cliente, con información denormalizada de usuario/evento")
// Datos de una entrada de lista de espera devueltos al cliente, con información denormalizada de usuario/evento
public class WaitingListResponseDTO {

    @Schema(description = "ID de la entrada de lista de espera", example = "1")
    private Integer id;

    @Schema(description = "ID del usuario en la lista de espera", example = "5")
    private Integer userId;

    @Schema(description = "Correo del usuario en la lista de espera", example = "usuario@correo.com")
    private String userEmail;

    @Schema(description = "Nombre del usuario en la lista de espera", example = "Juan Pérez")
    private String userName;

    @Schema(description = "ID de la localidad a la que pertenece la entrada", example = "2")
    private Long localityId;

    @Schema(description = "Nombre de la localidad", example = "VIP")
    private String localityName;

    @Schema(description = "ID del evento al que pertenece la localidad", example = "1")
    private Integer eventId;

    @Schema(description = "Nombre del evento", example = "Concierto de Rock")
    private String eventName;

    @Schema(description = "Estado actual de la entrada en la lista de espera")
    private WaitingListStatus status;

    @Schema(description = "Fecha y hora en la que se notificó al usuario que hay cupo disponible; null si aún no ha sido notificado")
    private LocalDateTime notifiedAt;

    @Schema(description = "Fecha y hora límite para que el usuario notificado complete su reserva (ventana de 30 minutos); null si aún no ha sido notificado")
    private LocalDateTime notificationExpiresAt;

    @Schema(description = "Fecha y hora en la que el usuario completó la reserva tras ser notificado; null si no aplica")
    private LocalDateTime fulfilledAt;

    @Schema(description = "Fecha y hora en la que se creó la entrada")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha y hora de la última actualización de la entrada")
    private LocalDateTime updatedAt;
}
