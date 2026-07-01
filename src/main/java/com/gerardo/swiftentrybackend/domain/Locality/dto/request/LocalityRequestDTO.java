package com.gerardo.swiftentrybackend.domain.Locality.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para crear una localidad. No se expone en un endpoint propio de LocalityController (que no tiene ruta de creación); " +
        "se usa como el campo 'localities' del payload de POST /swift_entry/events para crear las localidades iniciales del evento")
// Datos para crear una localidad como parte del payload de creación/actualización de un evento.
public class LocalityRequestDTO {

    /*@NotNull(message = "Event id is required")
    private Integer eventId;*/

    @Schema(description = "Nombre de la localidad", example = "VIP")
    @NotBlank(message = "Locality name is required")
    @Size(max = 100, message = "Locality name cannot exceed 100 characters")
    private String name;

    @Schema(description = "Descripción de la localidad", example = "Zona frontal con acceso preferencial")
    private String description;

    @Schema(description = "Precio de la localidad", example = "50.00")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be zero or greater")
    private BigDecimal price;

    @Schema(description = "Porcentaje de descuento por compra anticipada (0-100)", example = "10.00")
    @DecimalMin(value = "0.00", message = "Early bird discount must be 0 or greater")
    @DecimalMax(value = "100.00", message = "Early bird discount cannot exceed 100")
    private BigDecimal earlyBirdDiscountPercentage;

    @Schema(description = "Fecha límite para aplicar el descuento por compra anticipada", example = "2026-11-01T23:59:59")
    private LocalDateTime earlyBirdDeadline;

    /*@NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;*/

   /* @NotNull(message = "Available slots is required")
    @Min(value = 0, message = "Available slots cannot be negative")
    private Integer availableSlots;*/
}
