package com.gerardo.swiftentrybackend.domain.Payment.dto.response;

import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentMethod;
import com.gerardo.swiftentrybackend.domain.Payment.enums.PaymentStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.Ticket.dto.response.TicketResponseDTO;
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
// Representación de un pago devuelta al cliente, incluyendo los tickets emitidos si fue aprobado
@Schema(description = "Representación de un pago devuelta al cliente, incluyendo los tickets emitidos si fue aprobado")
public class PaymentResponseDTO {

    @Schema(description = "ID del pago", example = "1")
    private Integer id;

    @Schema(description = "ID de la reserva pagada", example = "1")
    private Integer reservationId;

    @Schema(description = "Monto total cobrado (incluye IVA del 13%)", example = "113.00")
    private BigDecimal amount;

    @Schema(description = "Método de pago utilizado", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;
    @Schema(description = "Estado de la reserva luego de procesar el pago", example = "CONFIRMED")
    private ReservationStatus reservationStatus;

    @Schema(description = "Estado del pago: APPROVED si la pasarela simulada lo aceptó, FAILED si lo rechazó", example = "APPROVED")
    private PaymentStatus status;

    @Schema(description = "Referencia de transacción generada al aprobar el pago (null si FAILED)", example = "TXN-3f8a1c2e-4b9d-4e2a-9c1f-6a2e7d8b1234")
    private String transactionReference;

    @Schema(description = "Fecha y hora en que se aprobó el pago (null si FAILED)")
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tickets issued when the payment is approved (empty otherwise)
    @Schema(description = "Tickets emitidos por este pago (uno por asiento); vacío si el pago fue FAILED")
    private List<TicketResponseDTO> tickets;
}
