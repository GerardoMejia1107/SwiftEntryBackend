package com.gerardo.swiftentrybackend.domain.Seat.services;

import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import com.gerardo.swiftentrybackend.domain.Event.repositories.EventRepository;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.SeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.utils.SeatMapper;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final LocalityRepository localityRepository;
    private final SeatMapper seatMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public List<SeatResponseDTO> createSeats(SeatRequestDTO request) {
        EventModel eventOfLocality = eventRepository.findById(request.getEventId())
                .orElseThrow();
        LocalityModel localityToFill = localityRepository.findByNameAndEvent_Id(request.getLocalityName(),
                eventOfLocality.getId());

        List<SeatModel> newSeats = new ArrayList<>();

        request.getRowLabels()
                .forEach(rowLabel -> {
                    for (int seatNumber = 1; seatNumber <= request.getSeatsPerRow(); seatNumber++) {
                        newSeats.add(
                                SeatModel.builder()
                                        .locality(localityToFill)
                                        .rowLabel(rowLabel)
                                        .seatNumber(String.valueOf(seatNumber))
                                        .status(SeatStatus.AVAILABLE)
                                        .isActive(true)
                                        .build()
                        );
                    }
                });

        List<SeatModel> savedSeats = seatRepository.saveAll(newSeats);

        localityToFill.setCapacity(savedSeats.size());
        localityRepository.save(localityToFill);

        return savedSeats.stream()
                .map(seatMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatResponseDTO> getAllSeats() {
        return List.of();
    }


    @Override
    public SeatResponseDTO getSeatById(Long id) {
        SeatModel seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat with id " + id + " not found"));
        return seatMapper.toResponse(seat);
    }

    @Override
    public List<SeatResponseDTO> getSeatsByLocalityId(Long localityId) {
        if (!localityRepository.existsById(localityId)) {
            throw new ResourceNotFoundException("Locality with id " + localityId + " not found");
        }
        return seatRepository.findAllByLocality_Id(localityId)
                .stream()
                .map(seatMapper::toResponse)
                .toList();
    }


    @Override
    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Seat with id " + id + " not found");
        }
        seatRepository.deleteById(id);
    }
}
