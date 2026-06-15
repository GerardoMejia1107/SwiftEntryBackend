package com.gerardo.swiftentrybackend.domain.Event.dto.response;

import com.gerardo.swiftentrybackend.domain.Event.enums.EventCategory;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private EventCategory category;
    private Integer organizerId;
    private String organizerName;
    //private Integer addressId;
    private EventStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String venueName;
    private String imageUrl;

    private String streetAddress;
    private String neighborhood;
    private String municipality;
    private String department;
    private String country;
    private String referencePoint;

    private List<LocalityResponseDTO> localities;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}