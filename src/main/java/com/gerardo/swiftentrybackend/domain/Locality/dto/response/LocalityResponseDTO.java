package com.gerardo.swiftentrybackend.domain.Locality.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de una localidad expuestos en las respuestas de la API")
// Datos de una localidad expuestos en las respuestas de la API.
public class LocalityResponseDTO {
    @Schema(description = "ID de la localidad", example = "1")
    private Long id;
    @Schema(description = "Nombre de la localidad", example = "VIP")
    private String name;
    @Schema(description = "Descripción de la localidad", example = "Zona frontal con acceso preferencial")
    private String description;
    @Schema(description = "Precio de la localidad", example = "50.00")
    private BigDecimal price;
    @Schema(description = "Porcentaje de descuento por compra anticipada (0-100)", example = "10.00")
    private BigDecimal earlyBirdDiscountPercentage;
    @Schema(description = "Fecha límite para aplicar el descuento por compra anticipada", example = "2026-11-01T23:59:59")
    private LocalDateTime earlyBirdDeadline;
    @Schema(description = "Capacidad total de la localidad (suma de asientos asignados)", example = "150")
    private Integer capacity;
    @Schema(description = "Asientos aún disponibles en la localidad", example = "120")
    private Integer availableSlots;
    @Schema(description = "Fecha de creación del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de última actualización del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
