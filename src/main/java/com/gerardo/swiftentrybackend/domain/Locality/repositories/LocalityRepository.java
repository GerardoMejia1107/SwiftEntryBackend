package com.gerardo.swiftentrybackend.domain.Locality.repositories;

import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio JPA de localidades, con consultas derivadas para buscarlas por evento y nombre.
public interface LocalityRepository extends JpaRepository<LocalityModel, Long> {
    // Verifica si ya existe una localidad con ese nombre dentro del mismo evento.
    boolean existsByNameAndEvent_Id(String name, Integer eventId);
    // Obtiene todas las localidades de un evento.
    List<LocalityModel> findAllByEvent_Id(Integer eventId);

    LocalityModel findByName(String name);

    LocalityModel findByNameAndEvent_Id(String name, Integer eventId);
}
