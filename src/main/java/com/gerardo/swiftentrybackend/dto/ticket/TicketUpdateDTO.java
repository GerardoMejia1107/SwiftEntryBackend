package com.gerardo.swiftentrybackend.dto.ticket;

import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketUpdateDTO {

    private TicketStatus status;

    private LocalDateTime usedAt;

    private Integer validatedById;

    private LocalDateTime validatedAt;
}
