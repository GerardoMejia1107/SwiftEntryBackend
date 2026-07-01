package com.gerardo.swiftentrybackend.domain.Event.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.Event.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Eventos", description = "Creación, consulta, actualización y eliminación de eventos junto con sus localidades")
@RestController
@RequestMapping("/swift_entry/events")
@RequiredArgsConstructor
// Controlador REST de eventos: creación, consulta, actualización y eliminación.
public class EventController {
    private final EventService eventService;
    private final ResponseBuilder responseBuilder;

    // Crea un evento junto con sus localidades; ruta pública.
    @Operation(summary = "Crear evento", description = "Ruta pública. Crea el evento y, en la misma operación, " +
            "guarda cada una de las localidades incluidas en el payload (con capacity/availableSlots en 0; los " +
            "asientos se asignan después mediante los endpoints de Asientos).")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (validación de campos)")
    })
    @PostMapping
    public ResponseEntity<GeneralResponse> createEvent(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del evento a crear junto con su lista inicial de localidades", required = true)
            EventRequestDTO eventRequestDTO) {
        EventResponseDTO response = eventService.createEvent(eventRequestDTO);
        return responseBuilder.buildResponse(
                "Event created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    // Lista todos los eventos; ruta pública.
    @Operation(summary = "Listar todos los eventos", description = "Ruta pública. Devuelve todos los eventos junto con sus localidades")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eventos recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllEvents() {
        List<EventResponseDTO> response = eventService.getAllEvents();
        return responseBuilder.buildResponse(
                "Events retrieved successfully",
                HttpStatus.OK,
                response
        );
    }

    // Lista los eventos creados por el organizador indicado.
    @Operation(summary = "Eventos por organizador", description = "Ruta autenticada (cualquier usuario logueado, sin rol específico). " +
            "Devuelve los eventos organizados por el usuario indicado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eventos recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class)))
    })
    @GetMapping("/organizer/{id}")
    public ResponseEntity<GeneralResponse> getAllEventsByOrganizerId(
            @Parameter(description = "ID del usuario organizador", example = "3") @PathVariable Integer id) {
        List<EventResponseDTO> response = eventService.getEventsByOrganizerId(id);
        return responseBuilder.buildResponse(
                "Events retrieved successfully",
                HttpStatus.OK,
                response
        );
    }

    // Obtiene el detalle de un evento con sus localidades.
    @Operation(summary = "Obtener evento por ID", description = "Ruta autenticada (cualquier usuario logueado). " +
            "Devuelve el evento junto con sus localidades")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento recuperado exitosamente",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un evento con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getEventById(
            @Parameter(description = "ID del evento", example = "1") @PathVariable Integer id) {
        EventResponseDTO response = eventService.getEventById(id);
        return responseBuilder.buildResponse(
                "Event retrieved successfully",
                HttpStatus.OK,
                response
        );
    }

    // Actualiza el evento y opcionalmente sincroniza sus localidades.
    @Operation(summary = "Actualizar evento", description = "Ruta autenticada. Actualiza los campos no nulos del evento; " +
            "si se envía la lista de localidades, sincroniza (crea/actualiza/elimina) las localidades del evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos, o localidad nueva sin 'name'/'price'"),
            @ApiResponse(responseCode = "403", description = "No se puede eliminar una localidad del evento porque ya tiene reservas"),
            @ApiResponse(responseCode = "404", description = "No existe el evento, el organizador o la localidad indicados"),
            @ApiResponse(responseCode = "409", description = "Conflicto de bloqueo optimista (@Version) al actualizar la localidad; reintentar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateEvent(
            @Parameter(description = "ID del evento", example = "1") @PathVariable Integer id,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Campos a actualizar del evento (los nulos se ignoran)", required = true)
            EventUpdateDTO request
    ) {
        EventResponseDTO response = eventService.updateEvent(id, request);
        return responseBuilder.buildResponse(
                "Event updated successfully",
                HttpStatus.OK,
                response
        );
    }

    // Elimina el evento; falla si ya tiene reservas asociadas.
    @Operation(summary = "Eliminar evento", description = "Ruta autenticada. Elimina el evento junto con sus localidades y asientos asignados; " +
            "rechaza el borrado si el evento ya tiene reservas (usar CANCELLED en su lugar)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No se puede eliminar el evento porque ya tiene reservas asociadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteEvent(
            @Parameter(description = "ID del evento", example = "1") @PathVariable Integer id) {
        eventService.deleteEvent(id);
        return responseBuilder.buildResponse(
                "Event deleted successfully",
                HttpStatus.OK,
                null
        );
    }
}
