package com.gerardo.swiftentrybackend.domain.Event.services;

import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventRequestDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.request.EventUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;

import java.util.List;

// Contrato de operaciones sobre eventos y sus localidades asociadas.
public interface EventService {
    // Crea un evento junto con sus localidades en una sola operación.
    EventResponseDTO createEvent(EventRequestDTO eventRequestDTO);

    // Devuelve todos los eventos con sus localidades.
    List<EventResponseDTO> getAllEvents();

    // Devuelve los eventos creados por un organizador.
    List<EventResponseDTO> getEventsByOrganizerId(Integer userId);

    // Busca un evento por id o lanza ResourceNotFoundException.
    EventResponseDTO getEventById(Integer id);

    // Actualiza campos del evento y, si se envían, sincroniza (crea/actualiza/elimina) sus localidades.
    EventResponseDTO updateEvent(Integer id, EventUpdateDTO request);

    // Elimina el evento y sus localidades; falla si ya existen reservas sobre alguna de sus localidades.
    void deleteEvent(Integer id);
}
