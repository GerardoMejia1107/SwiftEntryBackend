package com.gerardo.swiftentrybackend.domain.Event.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.Event.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Eventos", description = "Creación y consulta de eventos")
@RestController
@RequestMapping("/swift_entry/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final ResponseBuilder responseBuilder;

    @Operation(summary = "Crear evento", description = "Ruta pública. Crea el evento junto con sus localidades")
    @PostMapping
    public ResponseEntity<GeneralResponse> createEvent(@Valid @RequestBody EventRequestDTO eventRequestDTO) {
        EventResponseDTO response = eventService.createEvent(eventRequestDTO);
        return responseBuilder.buildResponse(
                "Event created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    @Operation(summary = "Listar todos los eventos", description = "Ruta pública")
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllEvents() {
        List<EventResponseDTO> response = eventService.getAllEvents();
        return responseBuilder.buildResponse(
                "Events retrieved successfully",
                HttpStatus.OK,
                response
        );
    }

    @Operation(summary = "Eventos por organizador")
    @GetMapping("/organizer/{id}")
    public ResponseEntity<GeneralResponse> getAllEventsByOrganizerId(@PathVariable Integer id) {
        List<EventResponseDTO> response = eventService.getEventsByOrganizerId(id);
        return responseBuilder.buildResponse(
                "Events retrieved successfully",
                HttpStatus.OK,
                response
        );
    }

    @Operation(summary = "Obtener evento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getEventById(@PathVariable Integer id) {
        EventResponseDTO response = eventService.getEventById(id);
        return responseBuilder.buildResponse(
                "Event retrieved successfully",
                HttpStatus.OK,
                response
        );
    }

    @Operation(summary = "Actualizar evento")
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateEvent(
            @PathVariable Integer id,
            @Valid @RequestBody EventUpdateDTO request
    ) {
        EventResponseDTO response = eventService.updateEvent(id, request);
        return responseBuilder.buildResponse(
                "Event updated successfully",
                HttpStatus.OK,
                response
        );
    }

    @Operation(summary = "Eliminar evento")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return responseBuilder.buildResponse(
                "Event deleted successfully",
                HttpStatus.OK,
                null
        );
    }
}
