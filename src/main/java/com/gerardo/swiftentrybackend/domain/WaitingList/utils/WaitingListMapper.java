package com.gerardo.swiftentrybackend.domain.WaitingList.utils;

import com.gerardo.swiftentrybackend.domain.WaitingList.WaitingListModel;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.response.WaitingListResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
// Convierte entidades de lista de espera a su DTO de respuesta
public class WaitingListMapper {

    // Mapea una entrada de lista de espera a su DTO, incluyendo datos denormalizados de usuario/localidad/evento
    public WaitingListResponseDTO toResponse(WaitingListModel model) {
        return WaitingListResponseDTO.builder()
                .id(model.getId())
                .userId(model.getUser().getId())
                .userEmail(model.getUser().getEmail())
                .userName(model.getUser().getName())
                .localityId(model.getLocality().getId())
                .localityName(model.getLocality().getName())
                .eventId(model.getLocality().getEvent().getId())
                .eventName(model.getLocality().getEvent().getName())
                .status(model.getStatus())
                .notifiedAt(model.getNotifiedAt())
                .notificationExpiresAt(model.getNotificationExpiresAt())
                .fulfilledAt(model.getFulfilledAt())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    // Mapea una lista de entradas de lista de espera a sus DTO de respuesta
    public List<WaitingListResponseDTO> toResponseList(List<WaitingListModel> models) {
        return models.stream().map(this::toResponse).toList();
    }
}
