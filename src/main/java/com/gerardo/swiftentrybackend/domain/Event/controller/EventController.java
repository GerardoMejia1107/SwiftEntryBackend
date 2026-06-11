package com.gerardo.swiftentrybackend.domain.Event.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import com.gerardo.swiftentrybackend.domain.Event.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swift_entry/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final ResponseBuilder responseBuilder;

    @PostMapping
    public ResponseEntity<GeneralResponse> createEvent(@Valid @RequestBody EventRequestDTO eventRequestDTO) {
        EventResponseDTO response = eventService.createEvent(eventRequestDTO);
        return responseBuilder.buildResponse(
                "Event created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> getAllEvents() {
        List<EventResponseDTO> response = eventService.getAllEvents();
        return responseBuilder.buildResponse(
                "Events retrieved successfully",
                HttpStatus.OK,
                response
        );
    }
}
