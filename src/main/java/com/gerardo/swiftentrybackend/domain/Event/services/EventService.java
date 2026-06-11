package com.gerardo.swiftentrybackend.domain.Event.services;

import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;

import java.util.List;

public interface EventService {
    EventResponseDTO createEvent(EventRequestDTO eventRequestDTO);

    List<EventResponseDTO> getAllEvents();

    EventResponseDTO getEventById(Integer id);

    EventResponseDTO updateEvent(Integer id, EventRequestDTO request);

    void deleteEvent(Integer id);
}
