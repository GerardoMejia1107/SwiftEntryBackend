package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para crear un ticket individual: reserva y asiento a los que pertenece.
@Schema(description = "PENDIENTE: DTO no utilizado actualmente por ningún endpoint. "
        + "Datos para crear un ticket individual: reserva y asiento a los que pertenece")
public class TicketRequestDTO {

    @Schema(description = "Id de la reserva a la que pertenece el ticket", example = "1")
    @NotNull(message = "Reservation id is required")
    private Integer reservationId;

    @Schema(description = "Id del asiento físico asociado al ticket", example = "10")
    @NotNull(message = "Seat id is required")
    private Integer seatId;
}
