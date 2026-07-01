package com.gerardo.swiftentrybackend.domain.Event.dto.request;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventCategory;
import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para actualizar parcialmente un evento (campos nulos se ignoran); si 'localities' viene, sincroniza (crea/actualiza/elimina) las localidades del evento")
// Datos para actualizar parcialmente un evento (campos nulos se ignoran); localities, si viene, sincroniza las localidades del evento.
public class EventUpdateDTO {

    @Schema(description = "Nuevo nombre del evento", example = "Concierto de Rock en la UCA (reprogramado)")
    @Size(max = 150, message = "Event name cannot exceed 150 characters")
    private String name;

    @Schema(description = "Nueva descripción del evento", example = "Concierto de bandas locales de rock alternativo")
    @Size(max = 1000, message = "Event description cannot exceed 1000 characters")
    private String description;

    @Schema(description = "Nueva categoría del evento")
    private EventCategory category;

    @Schema(description = "Nuevo ID del organizador del evento", example = "3")
    private Integer organizerId;

    @Schema(description = "Nueva fecha y hora de inicio del evento", example = "2026-12-02T19:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Nueva fecha y hora de finalización del evento", example = "2026-12-02T23:00:00")
    private LocalDateTime endDate;

    @Schema(description = "Nuevo nombre del recinto", example = "Universidad Centroamericana José Simeón Cañas")
    @Size(max = 150, message = "Venue name cannot exceed 150 characters")
    private String venueName;

    @Schema(description = "Nuevo estado del evento")
    private EventStatus status;

    @Schema(description = "Nueva URL de la imagen promocional del evento", example = "https://cdn.swiftentry.com/events/concierto.jpg")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @Schema(description = "Lista completa de localidades a sincronizar con el evento; los elementos con 'id' se actualizan, los que faltan se eliminan (si no tienen reservas) y los que vienen sin 'id' se crean")
    @Valid
    private List<LocalityUpdateDTO> localities;
}
