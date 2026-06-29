package com.gerardo.swiftentrybackend.domain.Reservation.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationRequestDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reservas", description = "Apartado temporal de asientos (expira en 15 min si no se paga)")
@RestController
@RequestMapping("/swift_entry/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ResponseBuilder responseBuilder;

    @Operation(summary = "Crear reserva", description = "Aparta los asientos seleccionados por 15 minutos. Máximo 5 asientos por reserva")
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

    @Operation(summary = "Listar todas las reservas", description = "Solo ADMINISTRATOR")
    // GET all — admin only (enforced via SecurityRoutes)
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllReservations() {
        List<ReservationResponseDTO> response = reservationService.getAllReservations();
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Mis reservas", description = "Lista las reservas del usuario autenticado")
    // GET my reservations — authenticated user sees only their own
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyReservations(Authentication authentication) {
        List<ReservationResponseDTO> response = reservationService.getMyReservations(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Reservas de mis eventos", description = "Para organizadores: muestra las reservas de todos sus eventos")
    // GET reservations for the authenticated organizer's events
    @GetMapping("/organizer")
    public ResponseEntity<GeneralResponse> getReservationsByOrganizer(Authentication authentication) {
        List<ReservationResponseDTO> response = reservationService.getReservationsByOrganizer(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Obtener reserva por ID", description = "Solo ADMINISTRATOR")
    // GET by id — admin only (enforced via SecurityRoutes)
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getReservationById(@PathVariable Integer id) {
        ReservationResponseDTO response = reservationService.getReservationById(id);
        return responseBuilder.buildResponse(
                "Reservation found successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Cancelar reserva", description = "Libera todos los asientos de la reserva")
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

    @Operation(summary = "Quitar un asiento de la reserva", description = "Si era el último asiento, cancela la reserva completa")
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
