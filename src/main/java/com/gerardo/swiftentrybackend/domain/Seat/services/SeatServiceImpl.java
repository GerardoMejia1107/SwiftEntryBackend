package com.gerardo.swiftentrybackend.domain.Seat.services;

import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Event.repositories.EventRepository;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationSeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.LocalitySeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.SeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatAssignmentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.LocalitySeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatMapResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.LocalitySeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.SeatRepository;
import com.gerardo.swiftentrybackend.domain.Seat.utils.SeatMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// Implementación de SeatService.
public class SeatServiceImpl implements SeatService {

    private static final List<String> ROWS = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
    private static final int COLUMNS = 16;

    private final SeatRepository seatRepository;
    private final LocalitySeatRepository localitySeatRepository;
    private final LocalityRepository localityRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final EventRepository eventRepository;
    private final SeatMapper seatMapper;

    // Genera los asientos de la grilla fija (filas A-J x 16 columnas) que aún no existan.
    @Override
    @Transactional
    public void initializeSeats() {
        Set<String> existing = seatRepository.findAll().stream()
                .map(s -> s.getRowLabel() + "-" + s.getSeatNumber())
                .collect(Collectors.toSet());

        List<SeatModel> toCreate = new ArrayList<>();
        for (String row : ROWS) {
            for (int col = 1; col <= COLUMNS; col++) {
                if (!existing.contains(row + "-" + col)) {
                    toCreate.add(SeatModel.builder()
                            .rowLabel(row)
                            .seatNumber(String.valueOf(col))
                            .build());
                }
            }
        }
        if (!toCreate.isEmpty()) {
            seatRepository.saveAll(toCreate);
        }
    }

    // Resuelve cada identificador de asiento (ej. "A1") y crea su LocalitySeat; valida que no esté ya asignado en el evento.
    @Override
    @Transactional
    public List<LocalitySeatResponseDTO> assignSeats(SeatAssignmentRequestDTO request) {
        LocalityModel locality = localityRepository.findById(request.getLocalityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Locality not found with id: " + request.getLocalityId()));

        Integer eventId = locality.getEvent().getId();

        List<LocalitySeatModel> assignments = new ArrayList<>();
        for (String identifier : request.getSeats()) {
            if (identifier == null || identifier.length() < 2) {
                throw new ResourceConflictException(
                        "Invalid seat identifier '" + identifier + "'. Expected format: row + column, e.g. A1, B10.");
            }
            String row = String.valueOf(identifier.charAt(0)).toUpperCase();
            String col = identifier.substring(1);

            SeatModel seat = seatRepository.findByRowLabelAndSeatNumber(row, col)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Seat " + identifier + " not found. Run POST /swift_entry/seats/initialize first."));

            if (localitySeatRepository.existsBySeat_IdAndLocality_Event_Id(seat.getId(), eventId)) {
                throw new ResourceConflictException(
                        "Seat " + identifier + " is already assigned to a locality in this event.");
            }

            assignments.add(LocalitySeatModel.builder()
                    .seat(seat)
                    .locality(locality)
                    .status(SeatStatus.AVAILABLE)
                    .isActive(true)
                    .build());
        }

        List<LocalitySeatModel> saved = localitySeatRepository.saveAll(assignments);

        locality.setCapacity(locality.getCapacity() + saved.size());
        locality.setAvailableSlots(locality.getAvailableSlots() + saved.size());
        localityRepository.save(locality);

        return saved.stream()
                .map(seatMapper::toLocalitySeatResponse)
                .collect(Collectors.toList());
    }

    // Combina todos los asientos físicos con su asignación (si existe) en el evento dado, ordenados por fila y columna.
    @Override
    public List<SeatMapResponseDTO> getSeatMapByEventId(Integer eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }

        Map<Long, LocalitySeatModel> assignmentsBySeatId =
                localitySeatRepository.findAllByLocality_Event_Id(eventId).stream()
                        .collect(Collectors.toMap(ls -> ls.getSeat().getId(), ls -> ls));

        return seatRepository.findAll().stream()
                .map(seat -> {
                    LocalitySeatModel assignment = assignmentsBySeatId.get(seat.getId());
                    return SeatMapResponseDTO.builder()
                            .seatId(seat.getId())
                            .row(seat.getRowLabel())
                            .col(seat.getSeatNumber())
                            .localitySeatId(assignment != null ? assignment.getId() : null)
                            .localityId(assignment != null ? assignment.getLocality().getId() : null)
                            .localityName(assignment != null ? assignment.getLocality().getName() : null)
                            .status(assignment != null ? assignment.getStatus() : null)
                            .build();
                })
                .sorted(Comparator.comparing(SeatMapResponseDTO::getRow)
                        .thenComparing(dto -> Integer.parseInt(dto.getCol())))
                .collect(Collectors.toList());
    }

    // Lista todos los asientos físicos.
    @Override
    public List<SeatResponseDTO> getAllSeats() {
        return seatRepository.findAll().stream()
                .map(seatMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Busca un asiento físico por id o lanza ResourceNotFoundException.
    @Override
    public SeatResponseDTO getSeatById(Long id) {
        SeatModel seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat with id " + id + " not found"));
        return seatMapper.toResponse(seat);
    }

    // Lista los LocalitySeat de una localidad; valida primero que la localidad exista.
    @Override
    public List<LocalitySeatResponseDTO> getSeatsByLocalityId(Long localityId) {
        if (!localityRepository.existsById(localityId)) {
            throw new ResourceNotFoundException("Locality with id " + localityId + " not found");
        }
        return localitySeatRepository.findAllByLocality_Id(localityId).stream()
                .map(seatMapper::toLocalitySeatResponse)
                .collect(Collectors.toList());
    }

    // Elimina el LocalitySeat y decrementa capacity/availableSlots de la localidad; rechaza si ya tiene reserva.
    @Override
    @Transactional
    public void unassignSeat(Long localitySeatId) {
        LocalitySeatModel localitySeat = localitySeatRepository.findById(localitySeatId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Locality seat assignment not found with id: " + localitySeatId));

        if (reservationSeatRepository.existsByLocalitySeat_Id(localitySeatId)) {
            throw new ForbiddenOperationException(
                    "Cannot unassign seat: it has an existing reservation.");
        }

        LocalityModel locality = localitySeat.getLocality();
        locality.setCapacity(locality.getCapacity() - 1);
        locality.setAvailableSlots(locality.getAvailableSlots() - 1);
        localityRepository.save(locality);

        localitySeatRepository.deleteById(localitySeatId);
    }

    // Elimina el asiento físico junto con sus asignaciones a localidades; rechaza si ya tiene reservas.
    @Override
    @Transactional
    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Seat with id " + id + " not found");
        }
        if (reservationSeatRepository.existsByLocalitySeat_Seat_Id(id)) {
            throw new ForbiddenOperationException(
                    "Cannot delete seat with id " + id + " because it has existing reservations.");
        }
        localitySeatRepository.deleteAllBySeat_Id(id);
        seatRepository.deleteById(id);
    }
}
