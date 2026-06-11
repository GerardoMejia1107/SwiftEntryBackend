package com.gerardo.swiftentrybackend.domain.Seat.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatUpdateDTO;
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

    @PostMapping
    public ResponseEntity<GeneralResponse> createSeat(@Valid @RequestBody SeatRequestDTO request) {
        SeatResponseDTO response = seatService.createSeat(request);
        return responseBuilder.buildResponse("Seat created successfully", HttpStatus.CREATED, response);
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
        List<SeatResponseDTO> response = seatService.getSeatsByLocalityId(localityId);
        return responseBuilder.buildResponse("Seats retrieved successfully", HttpStatus.OK, response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateSeat(
            @PathVariable Long id,
            @Valid @RequestBody SeatUpdateDTO request
    ) {
        SeatResponseDTO response = seatService.updateSeat(id, request);
        return responseBuilder.buildResponse("Seat updated successfully", HttpStatus.OK, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return responseBuilder.buildResponse("Seat deleted successfully", HttpStatus.OK, null);
    }
}
