package com.gerardo.swiftentrybackend.domain.Locality.services;

import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.repositories.EventRepository;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityRequestDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Locality.utils.LocalityMapper;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalityServiceImpl implements LocalityService {

    private final LocalityRepository localityRepository;
    private final EventRepository eventRepository;
    private final LocalityMapper localityMapper;

    @Override
    public LocalityResponseDTO createLocality(LocalityRequestDTO request) {
        EventModel event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + request.getEventId() + " not found"));

        if (localityRepository.existsByNameAndEvent_Id(request.getName(), request.getEventId())) {
            throw new ResourceConflictException("Locality '" + request.getName() + "' already exists for this event");
        }

        LocalityModel locality = localityMapper.toModel(request, event);
        return localityMapper.toResponse(localityRepository.save(locality));
    }

    @Override
    public List<LocalityResponseDTO> getAllLocalities() {
        return localityRepository.findAll()
                .stream()
                .map(localityMapper::toResponse)
                .toList();
    }

    @Override
    public LocalityResponseDTO getLocalityById(Long id) {
        LocalityModel locality = localityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Locality with id " + id + " not found"));
        return localityMapper.toResponse(locality);
    }

    @Override
    public List<LocalityResponseDTO> getLocalitiesByEventId(Integer eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event with id " + eventId + " not found");
        }
        return localityRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(localityMapper::toResponse)
                .toList();
    }

    @Override
    public LocalityResponseDTO updateLocality(Long id, LocalityUpdateDTO request) {
        LocalityModel locality = localityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Locality with id " + id + " not found"));

        localityMapper.updateModel(locality, request);
        return localityMapper.toResponse(localityRepository.save(locality));
    }

    @Override
    public void deleteLocality(Long id) {
        if (!localityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Locality with id " + id + " not found");
        }
        localityRepository.deleteById(id);
    }
}
