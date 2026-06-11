package com.gerardo.swiftentrybackend.domain.Event.dto.response;

import com.gerardo.swiftentrybackend.domain.Event.enums.EventCategory;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import lombok.*;

import java.time.LocalDateTime;

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

    private Integer addressId;

    private String venueName;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private EventStatus status;

    private String imageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}