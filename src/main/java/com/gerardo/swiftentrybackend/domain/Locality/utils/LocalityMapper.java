package com.gerardo.swiftentrybackend.domain.Locality.utils;

import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityRequestDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import org.springframework.stereotype.Component;

@Component
// Convierte entre LocalityModel y sus DTOs de request/response.
public class LocalityMapper {

    // Construye una LocalityModel nueva a partir del request de creación (capacity/availableSlots inician en 0).
    public LocalityModel toModel(LocalityRequestDTO request, EventModel event) {
        return LocalityModel.builder()
                .event(event)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .earlyBirdDiscountPercentage(request.getEarlyBirdDiscountPercentage())
                .earlyBirdDeadline(request.getEarlyBirdDeadline())
                .capacity(0)
                .availableSlots(0)
                .build();
    }

    // Construye una LocalityModel a partir del DTO de actualización (variante usada al reemplazar localidades vía Event).
    public LocalityModel toModel(LocalityUpdateDTO dto, EventModel event) {
        return LocalityModel.builder()
                .event(event)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .earlyBirdDiscountPercentage(dto.getEarlyBirdDiscountPercentage())
                .earlyBirdDeadline(dto.getEarlyBirdDeadline())
                .capacity(0)
                .availableSlots(0)
                .build();
    }

    // Convierte una LocalityModel a su DTO de respuesta.
    public LocalityResponseDTO toResponse(LocalityModel locality) {
        return LocalityResponseDTO.builder()
                .id(locality.getId())
                .name(locality.getName())
                .description(locality.getDescription())
                .price(locality.getPrice())
                .earlyBirdDiscountPercentage(locality.getEarlyBirdDiscountPercentage())
                .earlyBirdDeadline(locality.getEarlyBirdDeadline())
                .capacity(locality.getCapacity())
                .availableSlots(locality.getAvailableSlots())
                .createdAt(locality.getCreatedAt())
                .updatedAt(locality.getUpdatedAt())
                .build();
    }

    // Aplica sobre la entidad existente solo los campos no nulos presentes en el DTO de actualización.
    public void updateModel(LocalityModel locality, LocalityUpdateDTO request) {
        if (request.getName() != null) locality.setName(request.getName());
        if (request.getDescription() != null) locality.setDescription(request.getDescription());
        if (request.getPrice() != null) locality.setPrice(request.getPrice());
        if (request.getEarlyBirdDiscountPercentage() != null) locality.setEarlyBirdDiscountPercentage(request.getEarlyBirdDiscountPercentage());
        if (request.getEarlyBirdDeadline() != null) locality.setEarlyBirdDeadline(request.getEarlyBirdDeadline());
        if (request.getCapacity() != null) locality.setCapacity(request.getCapacity());
        if (request.getAvailableSlots() != null) locality.setAvailableSlots(request.getAvailableSlots());
    }
}
