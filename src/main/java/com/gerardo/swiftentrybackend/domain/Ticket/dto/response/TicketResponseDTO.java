package com.gerardo.swiftentrybackend.domain.Ticket.dto.response;

import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representación de un ticket para respuestas de API, incluyendo asiento, evento y validación.
@Schema(description = "Representación de un ticket para respuestas de API, incluyendo asiento, evento y validación")
public class TicketResponseDTO {

    @Schema(description = "Id del ticket", example = "1")
    private Integer id;

    @Schema(description = "Id de la reserva a la que pertenece el ticket", example = "5")
    private Integer reservationId;

    @Schema(description = "Id del asiento físico asociado", example = "10")
    private Long seatId;
    @Schema(description = "Número del asiento", example = "12")
    private String seatNumber;
    @Schema(description = "Fila del asiento", example = "A")
    private String rowLabel;

    @Schema(description = "Nombre del evento", example = "Concierto de Rock")
    private String eventName;
    @Schema(description = "Nombre de la localidad", example = "VIP")
    private String localityName;

    @Schema(description = "Email del titular actual del ticket (dueño original o receptor de una transferencia)", example = "usuario@example.com")
    private String currentHolderEmail;

    @Schema(description = "Código único del ticket", example = "TKT-3f2a9c1e-4b5d-4e6f-8a9b-1234567890ab")
    private String ticketCode;
    @Schema(description = "Código QR vigente del ticket", example = "QR-3f2a9c1e-4b5d-4e6f-8a9b-1234567890ab")
    private String qrCode;

    @Schema(description = "Estado actual del ticket", example = "ISSUED")
    private TicketStatus status;

    @Schema(description = "Fecha y hora de emisión del ticket")
    private LocalDateTime issuedAt;
    @Schema(description = "Fecha y hora en la que el ticket fue usado (escaneado en puerta)")
    private LocalDateTime usedAt;

    @Schema(description = "Id del usuario que validó el ticket", example = "3")
    private Integer validatedById;
    @Schema(description = "Nombre del usuario que validó el ticket", example = "Juan Pérez")
    private String validatedByName;

    @Schema(description = "Fecha y hora en la que se validó el ticket")
    private LocalDateTime validatedAt;

    @Schema(description = "Fecha de creación del registro")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de última actualización del registro")
    private LocalDateTime updatedAt;
}
