package com.gerardo.swiftentrybackend.domain.Ticket.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketTransferResponseDTO {

    private Integer transferId;
    private LocalDateTime transferredAt;

    private String fromUserEmail;
    private String toUserEmail;

    private TicketResponseDTO ticket;
}
