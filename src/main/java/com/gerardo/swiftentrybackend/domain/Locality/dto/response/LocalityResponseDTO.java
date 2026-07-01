package com.gerardo.swiftentrybackend.domain.Locality.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos de una localidad expuestos en las respuestas de la API.
public class LocalityResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal earlyBirdDiscountPercentage;
    private LocalDateTime earlyBirdDeadline;
    private Integer capacity;
    private Integer availableSlots;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
