package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de un asiento físico de la grilla del venue, expuestos en las respuestas de la API")
// Datos de un asiento físico expuestos en las respuestas de la API.
public class SeatResponseDTO {
    @Schema(description = "ID del asiento físico", example = "1")
    private Long id;
    @Schema(description = "Número (columna) del asiento dentro de su fila", example = "1")
    private String seatNumber;
    @Schema(description = "Etiqueta de la fila del asiento", example = "A")
    private String rowLabel;
    @Schema(description = "Fecha de creación del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de última actualización del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
