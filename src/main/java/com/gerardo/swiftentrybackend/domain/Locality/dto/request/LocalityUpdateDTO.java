package com.gerardo.swiftentrybackend.domain.Locality.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para actualizar parcialmente una localidad (campos nulos se ignoran). Se usa tanto en PUT /swift_entry/localities/{id} " +
        "como en el campo 'localities' de PUT /swift_entry/events/{id}: un 'id' nulo indica una localidad nueva a crear al sincronizar vía Event")
// Datos para actualizar parcialmente una localidad (campos nulos se ignoran); id nulo indica una localidad nueva al sincronizar vía Event.
public class LocalityUpdateDTO {

    @Schema(description = "ID de la localidad a actualizar; si es nulo (solo al sincronizar vía Event), se crea una localidad nueva", example = "1")
    private Long id;

    @Schema(description = "Nuevo nombre de la localidad", example = "VIP")
    @Size(max = 100, message = "Locality name cannot exceed 100 characters")
    private String name;

    @Schema(description = "Nueva descripción de la localidad", example = "Zona frontal con acceso preferencial")
    private String description;

    @Schema(description = "Nuevo precio de la localidad", example = "55.00")
    @DecimalMin(value = "0.00", message = "Price must be zero or greater")
    private BigDecimal price;

    @Schema(description = "Nuevo porcentaje de descuento por compra anticipada (0-100)", example = "10.00")
    @DecimalMin(value = "0.00", message = "Early bird discount must be 0 or greater")
    @DecimalMax(value = "100.00", message = "Early bird discount cannot exceed 100")
    private BigDecimal earlyBirdDiscountPercentage;

    @Schema(description = "Nueva fecha límite del descuento por compra anticipada", example = "2026-11-01T23:59:59")
    private LocalDateTime earlyBirdDeadline;

    @Schema(description = "Nueva capacidad total de la localidad", example = "150")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @Schema(description = "Nuevos asientos disponibles de la localidad", example = "150")
    @Min(value = 0, message = "Available slots cannot be negative")
    private Integer availableSlots;
}
