package com.gerardo.swiftentrybackend.domain.Reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representación de un asiento dentro de una reserva, con su ubicación y precio congelado
@Schema(description = "Representación de un asiento dentro de una reserva, con su ubicación y precio congelado")
public class ReservationSeatResponseDTO {

    @Schema(description = "ID de la línea ReservationSeat", example = "3")
    private Integer id;

    @Schema(description = "ID de la reserva a la que pertenece este asiento", example = "1")
    private Integer reservationId;

    @Schema(description = "ID del LocalitySeat reservado", example = "10")
    private Long seatId;
    @Schema(description = "Número del asiento físico", example = "12")
    private String seatNumber;
    @Schema(description = "Fila del asiento físico", example = "A")
    private String rowLabel;

    @Schema(description = "ID de la localidad a la que pertenece el asiento", example = "2")
    private Long localityId;
    @Schema(description = "Nombre de la localidad", example = "VIP")
    private String localityName;

    @Schema(description = "Precio congelado del asiento al momento de la reserva (incluye descuento early bird si aplicaba)", example = "45.00")
    private BigDecimal priceAtReservation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
