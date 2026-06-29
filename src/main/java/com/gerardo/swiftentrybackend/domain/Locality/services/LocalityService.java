package com.gerardo.swiftentrybackend.domain.Locality.services;

import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;

import java.util.List;

public interface LocalityService {
    List<LocalityResponseDTO> getAllLocalities();
    LocalityResponseDTO getLocalityById(Long id);
    List<LocalityResponseDTO> getLocalitiesByEventId(Integer eventId);
    LocalityResponseDTO updateLocality(Long id, LocalityUpdateDTO request);
    void deleteLocality(Long id);
}
