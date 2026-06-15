package com.gerardo.swiftentrybackend.domain.Event.dto.request;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventCategory;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUpdateDTO {

    @Size(max = 150, message = "Event name cannot exceed 150 characters")
    private String name;

    @Size(max = 1000, message = "Event description cannot exceed 1000 characters")
    private String description;

    private EventCategory category;

    @Valid
    private AddressRequestDTO address;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Size(max = 150, message = "Venue name cannot exceed 150 characters")
    private String venueName;

    private EventStatus status;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @Valid
    private List<LocalityUpdateDTO> localities;
}
