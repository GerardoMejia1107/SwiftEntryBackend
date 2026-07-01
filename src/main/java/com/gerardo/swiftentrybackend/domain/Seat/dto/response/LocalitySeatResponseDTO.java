package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de la asignación de un asiento físico a una localidad (LocalitySeat), incluyendo su estado actual de disponibilidad")
// Datos de la asignación de un asiento físico a una localidad, incluyendo su estado actual de disponibilidad.
public class LocalitySeatResponseDTO {
    @Schema(description = "ID de la asignación LocalitySeat", example = "1")
    private Long localitySeatId;
    @Schema(description = "ID del asiento físico", example = "1")
    private Long seatId;
    @Schema(description = "Número (columna) del asiento dentro de su fila", example = "1")
    private String seatNumber;
    @Schema(description = "Etiqueta de la fila del asiento", example = "A")
    private String rowLabel;
    @Schema(description = "ID de la localidad a la que está asignado el asiento", example = "1")
    private Long localityId;
    @Schema(description = "Nombre de la localidad a la que está asignado el asiento", example = "VIP")
    private String localityName;
    @Schema(description = "Estado actual del asiento (AVAILABLE/RESERVED/SOLD)")
    private SeatStatus status;
    @Schema(description = "Indica si la asignación está activa", example = "true")
    private Boolean isActive;
    @Schema(description = "Fecha de creación del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de última actualización del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
