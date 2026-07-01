package com.gerardo.swiftentrybackend.domain.Seat.services;

import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatAssignmentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.LocalitySeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatMapResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;

import java.util.List;

// Contrato de operaciones sobre la grilla física de asientos y su asignación a localidades.
public interface SeatService {
    // Crea los asientos físicos faltantes de la grilla del venue (idempotente).
    void initializeSeats();
    // Vincula asientos físicos a una localidad como LocalitySeat en estado AVAILABLE, incrementando capacity/availableSlots.
    List<LocalitySeatResponseDTO> assignSeats(SeatAssignmentRequestDTO request);
    // Devuelve el mapa completo de asientos de un evento con su estado (o sin asignar si no tiene LocalitySeat).
    List<SeatMapResponseDTO> getSeatMapByEventId(Integer eventId);
    // Lista todos los asientos físicos.
    List<SeatResponseDTO> getAllSeats();
    // Busca un asiento físico por id o lanza ResourceNotFoundException.
    SeatResponseDTO getSeatById(Long id);
    // Lista los LocalitySeat de una localidad.
    List<LocalitySeatResponseDTO> getSeatsByLocalityId(Long localityId);
    // Elimina la asignación de un asiento a su localidad, decrementando capacity/availableSlots; falla si tiene reservas.
    void unassignSeat(Long localitySeatId);
    // Elimina un asiento físico y sus asignaciones; falla si tiene reservas asociadas.
    void deleteSeat(Long id);
}
