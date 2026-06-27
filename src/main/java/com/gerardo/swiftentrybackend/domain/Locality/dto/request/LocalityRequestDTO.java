package com.gerardo.swiftentrybackend.domain.Locality.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalityRequestDTO {

    /*@NotNull(message = "Event id is required")
    private Integer eventId;*/

    @NotBlank(message = "Locality name is required")
    @Size(max = 100, message = "Locality name cannot exceed 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be zero or greater")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Early bird discount must be 0 or greater")
    @DecimalMax(value = "100.00", message = "Early bird discount cannot exceed 100")
    private BigDecimal earlyBirdDiscountPercentage;

    private LocalDateTime earlyBirdDeadline;

    /*@NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;*/

   /* @NotNull(message = "Available slots is required")
    @Min(value = 0, message = "Available slots cannot be negative")
    private Integer availableSlots;*/
}
