package com.gerardo.swiftentrybackend.dto.ticket;

import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {

    private Integer id;

    private Integer reservationId;

    private Long seatId;
    private String seatNumber;
    private String rowLabel;

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
