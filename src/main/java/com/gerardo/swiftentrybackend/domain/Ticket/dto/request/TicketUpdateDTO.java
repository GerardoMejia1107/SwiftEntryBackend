package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para actualizar parcialmente un ticket (estado, validación, uso).
public class TicketUpdateDTO {

    private TicketStatus status;

    private LocalDateTime usedAt;

    private Integer validatedById;

    private LocalDateTime validatedAt;
}
