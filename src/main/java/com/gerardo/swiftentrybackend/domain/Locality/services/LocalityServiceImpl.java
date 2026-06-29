package com.gerardo.swiftentrybackend.domain.Locality.services;

import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.domain.Event.repositories.EventRepository;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Locality.utils.LocalityMapper;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationSeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.LocalitySeatRepository;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalityServiceImpl implements LocalityService {

    private final LocalityRepository localityRepository;
    private final EventRepository eventRepository;
    private final LocalityMapper localityMapper;
    private final LocalitySeatRepository localitySeatRepository;
    private final ReservationSeatRepository reservationSeatRepository;

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
    @Transactional
    public void deleteLocality(Long id) {
        if (!localityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Locality with id " + id + " not found");
        }
        if (reservationSeatRepository.existsByLocalitySeat_Locality_Id(id)) {
            throw new ForbiddenOperationException(
                    "Cannot delete locality with id " + id + " because it has existing reservations.");
        }
        localitySeatRepository.deleteAllByLocality_Id(id);
        localityRepository.deleteById(id);
    }
}
