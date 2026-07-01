package com.gerardo.swiftentrybackend.domain.Event.utils;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.utils.LocalityMapper;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
// Convierte entre EventModel y sus DTOs de request/response, incluyendo el mapeo anidado de localidades.
public class EventMapper {

    private final LocalityMapper localityMapper;

    // Construye el DTO de respuesta del evento junto con sus localidades ya mapeadas.
    public EventResponseDTO toResponse(EventModel event, List<LocalityModel> localityModels) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .category(event.getCategory())
                .organizerId(event.getOrganizer() != null ? event.getOrganizer()
                        .getId() : null)
                .organizerName(event.getOrganizer() != null
                        ? event.getOrganizer()
                        .getName() + " " + event.getOrganizer()
                        .getLastName()
                        : null)
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .status(event.getStatus())
                .venueName(event.getVenueName())
                .localities(localityModels.stream()
                        .map(localityMapper::toResponse)
                        .toList())
                .imageUrl(event.getImageUrl())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    // Sobrescribe todos los campos del evento existente con los valores del request (no hace merge parcial).
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