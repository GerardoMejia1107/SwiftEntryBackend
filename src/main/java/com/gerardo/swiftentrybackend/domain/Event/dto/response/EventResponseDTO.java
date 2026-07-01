package com.gerardo.swiftentrybackend.domain.Event.dto.response;

import com.gerardo.swiftentrybackend.domain.Event.enums.EventCategory;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de un evento expuestos en las respuestas de la API, incluyendo sus localidades")
// Datos de un evento expuestos en las respuestas de la API, incluyendo sus localidades.
public class EventResponseDTO {
    @Schema(description = "ID del evento", example = "1")
    private Integer id;
    @Schema(description = "Nombre del evento", example = "Concierto de Rock en la UCA")
    private String name;
    @Schema(description = "Descripción del evento", example = "Concierto de bandas locales de rock alternativo")
    private String description;
    @Schema(description = "Categoría del evento")
    private EventCategory category;
    @Schema(description = "ID del usuario organizador", example = "3")
    private Integer organizerId;
    @Schema(description = "Nombre del usuario organizador", example = "Juan Pérez")
    private String organizerName;
    //private Integer addressId;
    @Schema(description = "Estado actual del evento")
    private EventStatus status;
    @Schema(description = "Fecha y hora de inicio del evento", example = "2026-12-01T19:00:00")
    private LocalDateTime startDate;
    @Schema(description = "Fecha y hora de finalización del evento", example = "2026-12-01T23:00:00")
    private LocalDateTime endDate;
    @Schema(description = "Nombre del recinto", example = "Universidad Centroamericana José Simeón Cañas")
    private String venueName;
    @Schema(description = "URL de la imagen promocional del evento", example = "https://cdn.swiftentry.com/events/concierto.jpg")
    private String imageUrl;

    @Schema(description = "Localidades del evento")
    private List<LocalityResponseDTO> localities;

    @Schema(description = "Fecha de creación del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de última actualización del registro", example = "2026-01-15T10:30:00")
    private LocalDateTime updatedAt;
}