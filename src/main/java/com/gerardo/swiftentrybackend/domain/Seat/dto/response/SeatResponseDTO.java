package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos de un asiento físico expuestos en las respuestas de la API.
public class SeatResponseDTO {
    private Long id;
    private String seatNumber;
    private String rowLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
