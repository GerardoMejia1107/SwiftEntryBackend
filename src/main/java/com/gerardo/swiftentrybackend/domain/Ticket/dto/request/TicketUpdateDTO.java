package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

import com.gerardo.swiftentrybackend.domain.Ticket.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para actualizar parcialmente un ticket (estado, validación, uso).
@Schema(description = "PENDIENTE: DTO no utilizado actualmente por ningún endpoint. "
        + "Datos para actualizar parcialmente un ticket (estado, validación, uso)")
public class TicketUpdateDTO {

    @Schema(description = "Nuevo estado del ticket", example = "USED")
    private TicketStatus status;

    @Schema(description = "Fecha y hora en la que se usó el ticket")
    private LocalDateTime usedAt;

    @Schema(description = "Id del usuario que validó el ticket (escaneo en puerta)", example = "3")
    private Integer validatedById;

    @Schema(description = "Fecha y hora en la que se validó el ticket")
    private LocalDateTime validatedAt;
}
