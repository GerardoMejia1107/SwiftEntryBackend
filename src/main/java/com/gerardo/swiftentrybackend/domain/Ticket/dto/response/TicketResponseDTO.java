package com.gerardo.swiftentrybackend.domain.Ticket.dto.response;

import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representación de un ticket para respuestas de API, incluyendo asiento, evento y validación.
public class TicketResponseDTO {

    private Integer id;

    private Integer reservationId;

    private Long seatId;
    private String seatNumber;
    private String rowLabel;

    private String eventName;
    private String localityName;

    private String currentHolderEmail;

    private String ticketCode;
    private String qrCode;

    private TicketStatus status;

    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;

    private Integer validatedById;
    private String validatedByName;

    private LocalDateTime validatedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
