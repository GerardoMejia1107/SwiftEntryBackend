package com.gerardo.swiftentrybackend.domain.Event.repositories;

import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.dto.response.EventResponseDTO;
import jdk.jfr.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio JPA de eventos.
public interface EventRepository extends JpaRepository<EventModel, Integer> {
    boolean existsByName(String name);

    // Obtiene todos los eventos creados por un organizador.
    List<EventModel> findAllByOrganizer_Id(Integer organizerId);
}
