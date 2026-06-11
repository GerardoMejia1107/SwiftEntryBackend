package com.gerardo.swiftentrybackend.domain.Locality.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalityResponseDTO {

    private Long id;

    private Integer eventId;

    private String eventName;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer capacity;

    private Integer availableSlots;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
