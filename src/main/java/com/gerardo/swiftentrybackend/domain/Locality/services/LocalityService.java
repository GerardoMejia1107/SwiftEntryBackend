package com.gerardo.swiftentrybackend.domain.Locality.services;

import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;

import java.util.List;

// Contrato de operaciones de consulta y administración de localidades (la creación real ocurre vía Event).
public interface LocalityService {
    // Devuelve todas las localidades existentes.
    List<LocalityResponseDTO> getAllLocalities();
    // Busca una localidad por id o lanza ResourceNotFoundException.
    LocalityResponseDTO getLocalityById(Long id);
    // Devuelve las localidades pertenecientes a un evento.
    List<LocalityResponseDTO> getLocalitiesByEventId(Integer eventId);
    // Actualiza parcialmente los campos no nulos de una localidad existente.
    LocalityResponseDTO updateLocality(Long id, LocalityUpdateDTO request);
    // Elimina una localidad junto con sus LocalitySeat; falla si ya tiene reservas asociadas.
    void deleteLocality(Long id);
}
