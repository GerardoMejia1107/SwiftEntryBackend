package com.gerardo.swiftentrybackend.domain.Seat.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatRequestDTO {
    @NotNull(message = "Locality id is required")
    private Long localityId;

    @NotNull(message = "Seats per row are required")
    private Integer seatsPerRow;

    @NotNull(message = "Row labels are required")
    @Size(min = 1, message = "At least one row label is required")
    private Set<String> rowLabels;
}