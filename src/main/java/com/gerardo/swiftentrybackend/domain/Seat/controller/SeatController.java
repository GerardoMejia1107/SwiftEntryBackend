package com.gerardo.swiftentrybackend.domain.Seat.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatAssignmentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.LocalitySeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatMapResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.services.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Asientos", description = "Grilla física de asientos y asignación a localidades")
@RestController
@RequestMapping("/swift_entry/seats")
@RequiredArgsConstructor
// Controlador REST de asientos: grilla física, asignación a localidades y mapa de estado por evento.
public class SeatController {

    private final SeatService seatService;
    private final ResponseBuilder responseBuilder;

    // Crea todos los asientos físicos del venue; se llama una sola vez.
    @Operation(summary = "Inicializar grilla de asientos", description = "Crea todos los asientos físicos del venue. Llamar una sola vez por venue")
    @PostMapping("/initialize")
    public ResponseEntity<GeneralResponse> initializeSeats() {
        seatService.initializeSeats();
        return responseBuilder.buildResponse("Seat grid initialized successfully", HttpStatus.CREATED, null);
    }

    // Vincula asientos físicos a una localidad, creando los LocalitySeat en estado AVAILABLE.
    @Operation(summary = "Asignar asientos a una localidad", description = "Vincula asientos físicos a una localidad; crea los registros en locality_seats con estado AVAILABLE")
    @PostMapping("/assign")
    public ResponseEntity<GeneralResponse> assignSeats(@Valid @RequestBody SeatAssignmentRequestDTO request) {
        List<LocalitySeatResponseDTO> response = seatService.assignSeats(request);
        return responseBuilder.buildResponse("Seats assigned successfully", HttpStatus.CREATED, response);
    }

    // Devuelve el mapa de asientos del evento con su estado actual.
    @Operation(summary = "Mapa de asientos de un evento", description = "Devuelve todos los locality_seats del evento con su estado actual (AVAILABLE/RESERVED/SOLD)")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> getSeatMapByEventId(@PathVariable Integer eventId) {
        List<SeatMapResponseDTO> response = seatService.getSeatMapByEventId(eventId);
        return responseBuilder.buildResponse("Seat map retrieved successfully", HttpStatus.OK, response);
    }

    // Lista todos los asientos físicos.
    @Operation(summary = "Listar todos los asientos")
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllSeats() {
        List<SeatResponseDTO> response = seatService.getAllSeats();
        return responseBuilder.buildResponse("Seats retrieved successfully", HttpStatus.OK, response);
    }

    // Obtiene un asiento físico por id.
    @Operation(summary = "Obtener asiento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getSeatById(@PathVariable Long id) {
        SeatResponseDTO response = seatService.getSeatById(id);
        return responseBuilder.buildResponse("Seat found successfully", HttpStatus.OK, response);
    }

    // Lista los LocalitySeat de una localidad.
    @Operation(summary = "Asientos de una localidad")
    @GetMapping("/locality/{localityId}")
    public ResponseEntity<GeneralResponse> getSeatsByLocalityId(@PathVariable Long localityId) {
        List<LocalitySeatResponseDTO> response = seatService.getSeatsByLocalityId(localityId);
        return responseBuilder.buildResponse("Seats retrieved successfully", HttpStatus.OK, response);
    }

    // Elimina el vínculo entre un asiento físico y su localidad.
    @Operation(summary = "Desasignar asiento de una localidad")
    @DeleteMapping("/assignment/{localitySeatId}")
    public ResponseEntity<GeneralResponse> unassignSeat(@PathVariable Long localitySeatId) {
        seatService.unassignSeat(localitySeatId);
        return responseBuilder.buildResponse("Seat unassigned successfully", HttpStatus.OK, null);
    }

    // Elimina un asiento físico del venue.
    @Operation(summary = "Eliminar asiento físico")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return responseBuilder.buildResponse("Seat deleted successfully", HttpStatus.OK, null);
    }
}
