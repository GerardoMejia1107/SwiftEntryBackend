package com.gerardo.swiftentrybackend.domain.Locality.dto.request;

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
// Datos para actualizar parcialmente una localidad (campos nulos se ignoran); id nulo indica una localidad nueva al sincronizar vía Event.
public class LocalityUpdateDTO {

    private Long id;

    @Size(max = 100, message = "Locality name cannot exceed 100 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.00", message = "Price must be zero or greater")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Early bird discount must be 0 or greater")
    @DecimalMax(value = "100.00", message = "Early bird discount cannot exceed 100")
    private BigDecimal earlyBirdDiscountPercentage;

    private LocalDateTime earlyBirdDeadline;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @Min(value = 0, message = "Available slots cannot be negative")
    private Integer availableSlots;
}
