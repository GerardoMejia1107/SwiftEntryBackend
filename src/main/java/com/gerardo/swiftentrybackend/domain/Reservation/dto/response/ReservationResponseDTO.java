package com.gerardo.swiftentrybackend.domain.Reservation.dto.response;

import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Representación de una reserva devuelta al cliente, con montos y asientos incluidos
@Schema(description = "Representación de una reserva devuelta al cliente, con montos y asientos incluidos")
public class ReservationResponseDTO {

    @Schema(description = "ID de la reserva", example = "1")
    private Integer id;

    @Schema(description = "ID del usuario dueño de la reserva", example = "3")
    private Integer userId;
    @Schema(description = "Nombre del usuario dueño de la reserva", example = "Juan Pérez")
    private String userName;
    @Schema(description = "Correo del usuario dueño de la reserva", example = "juan.perez@example.com")
    private String userEmail;

    @Schema(description = "Estado actual de la reserva", example = "PENDING")
    private ReservationStatus status;

    @Schema(description = "Suma de los precios de los asientos (sin impuestos)", example = "100.00")
    private BigDecimal subtotal;
    @Schema(description = "Monto de IVA (13% del subtotal)", example = "13.00")
    private BigDecimal taxAmount;
    @Schema(description = "Descuento total aplicado (por ejemplo, early bird)", example = "0.00")
    private BigDecimal discountAmount;
    @Schema(description = "Monto total a pagar (subtotal + impuestos)", example = "113.00")
    private BigDecimal totalAmount;

    @Schema(description = "Fecha y hora en que se creó la reserva")
    private LocalDateTime reservedAt;
    @Schema(description = "Fecha y hora en que se completó la compra (null si aún no se ha pagado)")
    private LocalDateTime purchasedAt;
    @Schema(description = "Fecha y hora en que expira la retención de los asientos (15 min tras la creación)")
    private LocalDateTime expiresAt;

    @Schema(description = "Asientos incluidos en la reserva")
    private List<ReservationSeatResponseDTO> reservationSeats;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
