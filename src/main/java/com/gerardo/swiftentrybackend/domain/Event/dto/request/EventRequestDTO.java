package com.gerardo.swiftentrybackend.domain.Event.dto.request;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventCategory;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos para crear un evento junto con su lista inicial de localidades (POST /swift_entry/events, ruta pública)")
// Datos para crear un evento junto con sus localidades iniciales.
public class EventRequestDTO {
    @Schema(description = "Nombre del evento", example = "Concierto de Rock en la UCA")
    @NotBlank(message = "Event name is required")
    @Size(max = 150, message = "Event name cannot exceed 150 characters")
    private String name;

    @Schema(description = "Descripción del evento", example = "Concierto de bandas locales de rock alternativo")
    @Size(max = 1000, message = "Event description cannot exceed 1000 characters")
    private String description;

    @Schema(description = "Categoría del evento")
    @NotNull(message = "Event category is required")
    private EventCategory category;

    @Schema(description = "ID del usuario que organiza el evento", example = "3")
    @NotNull(message = "Organizer id is required")
    private Integer organizerId;

    @Schema(description = "Fecha y hora de inicio del evento; debe ser futura", example = "2026-12-01T19:00:00")
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @Schema(description = "Fecha y hora de finalización del evento; debe ser futura", example = "2026-12-01T23:00:00")
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @Schema(description = "Nombre del recinto (informativo; el venue real está fijo en el servicio)", example = "Universidad Centroamericana José Simeón Cañas")
    @Size(max = 150, message = "Venue name cannot exceed 150 characters")
    private String venueName;

    @Schema(description = "Estado inicial del evento")
    @NotNull(message = "Event status is required")
    private EventStatus status;

    @Schema(description = "URL de la imagen promocional del evento", example = "https://cdn.swiftentry.com/events/concierto.jpg")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @Schema(description = "Localidades iniciales del evento; se crean junto con él (al menos una es requerida)")
    @NotNull(message = "At least one locality is required")
    @Size(min = 1, message = "At least one locality is required")
    private List<LocalityRequestDTO> localities;
}
