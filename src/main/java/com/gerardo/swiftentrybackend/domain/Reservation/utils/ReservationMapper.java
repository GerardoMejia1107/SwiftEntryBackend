package com.gerardo.swiftentrybackend.domain.Reservation.utils;

import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Conversión entre ReservationModel y sus DTOs de entrada/salida
@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final ReservationSeatMapper reservationSeatMapper;

    // Construye un ReservationModel nuevo a partir de los montos ya calculados, sin id de persistencia
    public ReservationModel toModel(
            UserModel user,
            ReservationStatus status,
            BigDecimal subtotal,
            BigDecimal taxAmount,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            LocalDateTime reservedAt,
            LocalDateTime expiresAt
    ) {
        return ReservationModel.builder()
                .user(user)
                .status(status)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .reservedAt(reservedAt)
                .expiresAt(expiresAt)
                .build();
    }

    // Mapea el modelo a su DTO de respuesta, incluyendo los asientos anidados
    public ReservationResponseDTO toResponse(ReservationModel model) {
        return ReservationResponseDTO.builder()
                .id(model.getId())
                .userId(model.getUser().getId())
                .userName(model.getUser().getName())
                .userEmail(model.getUser().getEmail())
                .status(model.getStatus())
                .subtotal(model.getSubtotal())
                .taxAmount(model.getTaxAmount())
                .discountAmount(model.getDiscountAmount())
                .totalAmount(model.getTotalAmount())
                .reservedAt(model.getReservedAt())
                .purchasedAt(model.getPurchasedAt())
                .expiresAt(model.getExpiresAt())
                .reservationSeats(
                        model.getReservationSeats()
                                .stream()
                                .map(reservationSeatMapper::toResponse)
                                .toList()
                )
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    public List<ReservationResponseDTO> toResponseList(List<ReservationModel> reservations) {
        return reservations.stream()
                .map(this::toResponse)
                .toList();
    }

    // Aplica al modelo solo los campos no nulos presentes en el DTO de actualización
    public void updateModel(ReservationModel model, ReservationUpdateDTO dto) {
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus());
        }

        if (dto.getDiscountAmount() != null) {
            model.setDiscountAmount(dto.getDiscountAmount());
        }

        if (dto.getPurchasedAt() != null) {
            model.setPurchasedAt(dto.getPurchasedAt());
        }
    }
}
