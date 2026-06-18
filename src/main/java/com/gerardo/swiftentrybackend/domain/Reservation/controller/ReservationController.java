package com.gerardo.swiftentrybackend.domain.Reservation.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationRequestDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swift_entry/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ResponseBuilder responseBuilder;

    // POST — create reservation for the authenticated user
    @PostMapping
    public ResponseEntity<GeneralResponse> createReservation(
            @Valid @RequestBody ReservationRequestDTO request,
            Authentication authentication
    ) {
        ReservationResponseDTO response = reservationService.createReservation(
                request, authentication.getName());
        return responseBuilder.buildResponse(
                "Reservation created successfully", HttpStatus.CREATED, response);
    }

    // GET all — admin only (enforced via SecurityRoutes)
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllReservations() {
        List<ReservationResponseDTO> response = reservationService.getAllReservations();
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    // GET my reservations — authenticated user sees only their own
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyReservations(Authentication authentication) {
        List<ReservationResponseDTO> response = reservationService.getMyReservations(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    // GET reservations for the authenticated organizer's events
    @GetMapping("/organizer")
    public ResponseEntity<GeneralResponse> getReservationsByOrganizer(Authentication authentication) {
        List<ReservationResponseDTO> response = reservationService.getReservationsByOrganizer(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    // GET by id — admin only (enforced via SecurityRoutes)
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getReservationById(@PathVariable Integer id) {
        ReservationResponseDTO response = reservationService.getReservationById(id);
        return responseBuilder.buildResponse(
                "Reservation found successfully", HttpStatus.OK, response);
    }

    // DELETE — cancels the reservation; service enforces ownership
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> cancelReservation(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        ReservationResponseDTO response = reservationService.cancelReservation(
                id, authentication.getName());
        return responseBuilder.buildResponse(
                "Reservation cancelled successfully", HttpStatus.OK, response);
    }

    // DELETE — removes a single seat from a PENDING reservation; auto-cancels if last seat
    @DeleteMapping("/{reservationId}/seats/{reservationSeatId}")
    public ResponseEntity<GeneralResponse> removeSeatFromReservation(
            @PathVariable Integer reservationId,
            @PathVariable Integer reservationSeatId,
            Authentication authentication
    ) {
        ReservationResponseDTO response = reservationService.removeSeatFromReservation(
                reservationId, reservationSeatId, authentication.getName());
        return responseBuilder.buildResponse(
                "Seat removed from reservation successfully", HttpStatus.OK, response);
    }
}
