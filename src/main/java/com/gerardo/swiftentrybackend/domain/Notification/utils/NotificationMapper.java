package com.gerardo.swiftentrybackend.domain.Notification.utils;

import com.gerardo.swiftentrybackend.domain.Notification.NotificationModel;
import com.gerardo.swiftentrybackend.domain.Notification.dto.response.NotificationResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toResponse(NotificationModel model) {
        return NotificationResponseDTO.builder()
                .id(model.getId())
                .userId(model.getUser().getId())
                .type(model.getType())
                .title(model.getTitle())
                .message(model.getMessage())
                .relatedEntityId(model.getRelatedEntityId())
                .read(model.getRead())
                .readAt(model.getReadAt())
                .createdAt(model.getCreatedAt())
                .build();
    }

    public List<NotificationResponseDTO> toResponseList(List<NotificationModel> models) {
        return models.stream().map(this::toResponse).toList();
    }
}
