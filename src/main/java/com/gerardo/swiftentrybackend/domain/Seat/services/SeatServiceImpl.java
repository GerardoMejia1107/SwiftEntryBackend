package com.gerardo.swiftentrybackend.domain.Seat.services;

import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.SeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.utils.SeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final LocalityRepository localityRepository;
    private final SeatMapper seatMapper;

    @Override
    public SeatResponseDTO createSeat(SeatRequestDTO request) {
        LocalityModel locality = localityRepository.findById(request.getLocalityId())
                .orElseThrow(() -> new RuntimeException("Locality with id " + request.getLocalityId() + " not found"));

        if (seatRepository.existsBySeatNumberAndLocality_Id(request.getSeatNumber(), request.getLocalityId())) {
            throw new RuntimeException("Seat '" + request.getSeatNumber() + "' already exists in this locality");
        }

        SeatModel seat = seatMapper.toModel(request, locality);
        return seatMapper.toResponse(seatRepository.save(seat));
    }

    @Override
    public List<SeatResponseDTO> getAllSeats() {
        return seatRepository.findAll()
                .stream()
                .map(seatMapper::toResponse)
                .toList();
    }

    @Override
    public SeatResponseDTO getSeatById(Long id) {
        SeatModel seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat with id " + id + " not found"));
        return seatMapper.toResponse(seat);
    }

    @Override
    public List<SeatResponseDTO> getSeatsByLocalityId(Long localityId) {
        if (!localityRepository.existsById(localityId)) {
            throw new RuntimeException("Locality with id " + localityId + " not found");
        }
        return seatRepository.findAllByLocality_Id(localityId)
                .stream()
                .map(seatMapper::toResponse)
                .toList();
    }

    @Override
    public SeatResponseDTO updateSeat(Long id, SeatUpdateDTO request) {
        SeatModel seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat with id " + id + " not found"));

        seatMapper.updateModel(seat, request);
        return seatMapper.toResponse(seatRepository.save(seat));
    }

    @Override
    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new RuntimeException("Seat with id " + id + " not found");
        }
        seatRepository.deleteById(id);
    }
}
