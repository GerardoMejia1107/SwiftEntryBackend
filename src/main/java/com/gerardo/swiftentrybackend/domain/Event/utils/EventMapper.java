package com.gerardo.swiftentrybackend.domain.Event.utils;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventModel toModel(
            EventRequestDTO request,
            UserModel organizer,
            AddressModel address
    ) {
        return EventModel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .organizer(organizer)
                .address(address)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .venueName(request.getVenueName())
                .status(request.getStatus())
                .imageUrl(request.getImageUrl())
                .build();
    }

    public EventResponseDTO toResponse(EventModel event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .category(event.getCategory())
                .organizerId(event.getOrganizer() != null ? event.getOrganizer().getId() : null)
                .organizerName(event.getOrganizer() != null
                        ? event.getOrganizer().getName() + " " + event.getOrganizer().getLastName()
                        : null)
                .addressId(event.getAddress() != null ? event.getAddress().getId() : null)
                .venueName(event.getVenueName())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .status(event.getStatus())
                .imageUrl(event.getImageUrl())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    public void updateModel(
            EventModel event,
            EventRequestDTO request,
            UserModel organizer,
            AddressModel address
    ) {
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setOrganizer(organizer);
        event.setAddress(address);
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setVenueName(request.getVenueName());
        event.setStatus(request.getStatus());
        event.setImageUrl(request.getImageUrl());
    }
}