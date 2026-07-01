package com.gerardo.swiftentrybackend.domain.Ticket.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representación de una transferencia de ticket completada, con el ticket resultante embebido.
@Schema(description = "Representación de una transferencia de ticket completada, con el ticket resultante embebido")
public class TicketTransferResponseDTO {

    @Schema(description = "Id del registro de transferencia", example = "1")
    private Integer transferId;
    @Schema(description = "Fecha y hora en la que se realizó la transferencia")
    private LocalDateTime transferredAt;

    @Schema(description = "Email del usuario que cedió el ticket", example = "emisor@example.com")
    private String fromUserEmail;
    @Schema(description = "Email del usuario que recibió el ticket", example = "receptor@example.com")
    private String toUserEmail;

    @Schema(description = "Ticket resultante tras la transferencia, con código y QR rotados")
    private TicketResponseDTO ticket;
}
