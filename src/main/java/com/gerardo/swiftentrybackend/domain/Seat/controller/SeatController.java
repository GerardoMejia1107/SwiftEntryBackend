package com.gerardo.swiftentrybackend.domain.Seat.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatAssignmentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.LocalitySeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatMapResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.services.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swift_entry/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
    private final ResponseBuilder responseBuilder;

    @PostMapping("/initialize")
    public ResponseEntity<GeneralResponse> initializeSeats() {
        seatService.initializeSeats();
        return responseBuilder.buildResponse("Seat grid initialized successfully", HttpStatus.CREATED, null);
    }

    @PostMapping("/assign")
    public ResponseEntity<GeneralResponse> assignSeats(@Valid @RequestBody SeatAssignmentRequestDTO request) {
        List<LocalitySeatResponseDTO> response = seatService.assignSeats(request);
        return responseBuilder.buildResponse("Seats assigned successfully", HttpStatus.CREATED, response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> getSeatMapByEventId(@PathVariable Integer eventId) {
        List<SeatMapResponseDTO> response = seatService.getSeatMapByEventId(eventId);
        return responseBuilder.buildResponse("Seat map retrieved successfully", HttpStatus.OK, response);
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> getAllSeats() {
        List<SeatResponseDTO> response = seatService.getAllSeats();
        return responseBuilder.buildResponse("Seats retrieved successfully", HttpStatus.OK, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getSeatById(@PathVariable Long id) {
        SeatResponseDTO response = seatService.getSeatById(id);
        return responseBuilder.buildResponse("Seat found successfully", HttpStatus.OK, response);
    }

    @GetMapping("/locality/{localityId}")
    public ResponseEntity<GeneralResponse> getSeatsByLocalityId(@PathVariable Long localityId) {
        List<LocalitySeatResponseDTO> response = seatService.getSeatsByLocalityId(localityId);
        return responseBuilder.buildResponse("Seats retrieved successfully", HttpStatus.OK, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return responseBuilder.buildResponse("Seat deleted successfully", HttpStatus.OK, null);
    }
}
