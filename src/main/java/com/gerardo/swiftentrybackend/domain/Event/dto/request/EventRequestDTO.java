package com.gerardo.swiftentrybackend.domain.Event.dto.request;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventCategory;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Datos para crear un evento junto con sus localidades iniciales.
public class EventRequestDTO {
    @NotBlank(message = "Event name is required")
    @Size(max = 150, message = "Event name cannot exceed 150 characters")
    private String name;

    @Size(max = 1000, message = "Event description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Event category is required")
    private EventCategory category;

    @NotNull(message = "Organizer id is required")
    private Integer organizerId;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @Size(max = 150, message = "Venue name cannot exceed 150 characters")
    private String venueName;

    @NotNull(message = "Event status is required")
    private EventStatus status;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @NotNull(message = "At least one locality is required")
    @Size(min = 1, message = "At least one locality is required")
    private List<LocalityRequestDTO> localities;
}
