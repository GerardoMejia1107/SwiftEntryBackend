package com.gerardo.swiftentrybackend.domain.Locality.utils;

import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityRequestDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class LocalityMapper {

    public LocalityModel toModel(LocalityRequestDTO request, EventModel event) {
        return LocalityModel.builder()
                .event(event)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .capacity(0)
                .availableSlots(0)
                .build();
    }

    public LocalityModel toModel(LocalityUpdateDTO dto, EventModel event) {
        return LocalityModel.builder()
                .event(event)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .capacity(0)
                .availableSlots(0)
                .build();
    }

    public LocalityResponseDTO toResponse(LocalityModel locality) {
        return LocalityResponseDTO.builder()
                .id(locality.getId())
                .name(locality.getName())
                .description(locality.getDescription())
                .price(locality.getPrice())
                .capacity(locality.getCapacity())
                .availableSlots(locality.getAvailableSlots())
                .createdAt(locality.getCreatedAt())
                .updatedAt(locality.getUpdatedAt())
                .build();
    }

    public void updateModel(LocalityModel locality, LocalityUpdateDTO request) {
        if (request.getName() != null) locality.setName(request.getName());
        if (request.getDescription() != null) locality.setDescription(request.getDescription());
        if (request.getPrice() != null) locality.setPrice(request.getPrice());
        if (request.getCapacity() != null) locality.setCapacity(request.getCapacity());
        if (request.getAvailableSlots() != null) locality.setAvailableSlots(request.getAvailableSlots());
    }
}
