package com.gerardo.swiftentrybackend.domain.Reservation.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationRequestDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints para crear, consultar y cancelar reservas de asientos
@Tag(name = "Reservas", description = "Apartado temporal de asientos (expira en 15 min si no se paga). Todos los endpoints requieren JWT")
@RestController
@RequestMapping("/swift_entry/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ResponseBuilder responseBuilder;

    @Operation(summary = "Crear reserva",
            description = "Aparta los asientos seleccionados por 15 minutos (bloqueo pesimista sobre los LocalitySeat). " +
                    "Máximo 5 asientos por reserva y máximo 5 asientos retenidos (PENDING o CONFIRMED) por usuario y evento. " +
                    "Si nadie paga antes de que expire, ReservationScheduler libera los asientos automáticamente cada 60s")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la solicitud inválidos (validación de campos)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario o alguno de los asientos no fue encontrado"),
            @ApiResponse(responseCode = "409", description = "Se excede el máximo de asientos permitido, algún asiento ya no está disponible, " +
                    "los asientos pertenecen a eventos distintos, o conflicto de bloqueo optimista")
    })
    // POST — create reservation for the authenticated user
    @PostMapping
    public ResponseEntity<GeneralResponse> createReservation(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Lista de IDs de LocalitySeat a reservar (máximo 5)", required = true)
            ReservationRequestDTO request,
            Authentication authentication
    ) {
        ReservationResponseDTO response = reservationService.createReservation(
                request, authentication.getName());
        return responseBuilder.buildResponse(
                "Reservation created successfully", HttpStatus.CREATED, response);
    }

    @Operation(summary = "Listar todas las reservas", description = "Solo ADMINISTRATOR. Devuelve todas las reservas del sistema, sin filtrar por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR")
    })
    // GET all — admin only (enforced via SecurityRoutes)
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllReservations() {
        List<ReservationResponseDTO> response = reservationService.getAllReservations();
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Mis reservas", description = "Lista las reservas del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    // GET my reservations — authenticated user sees only their own
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyReservations(Authentication authentication) {
        List<ReservationResponseDTO> response = reservationService.getMyReservations(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Reservas de mis eventos", description = "Para organizadores: muestra las reservas de todos los eventos que organiza el usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    // GET reservations for the authenticated organizer's events
    @GetMapping("/organizer")
    public ResponseEntity<GeneralResponse> getReservationsByOrganizer(Authentication authentication) {
        List<ReservationResponseDTO> response = reservationService.getReservationsByOrganizer(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Reservations retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Obtener reserva por ID", description = "Solo ADMINISTRATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    // GET by id — admin only (enforced via SecurityRoutes)
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getReservationById(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Integer id) {
        ReservationResponseDTO response = reservationService.getReservationById(id);
        return responseBuilder.buildResponse(
                "Reservation found successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Cancelar reserva",
            description = "Libera todos los asientos de la reserva (vuelven a AVAILABLE) y la marca como CANCELLED. " +
                    "Solo el dueño de la reserva puede cancelarla, y solo si está en estado PENDING")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "La reserva no está en estado PENDING"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada o no pertenece al usuario autenticado")
    })
    // DELETE — cancels the reservation; service enforces ownership
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> cancelReservation(
            @Parameter(description = "ID de la reserva a cancelar", example = "1")
            @PathVariable Integer id,
            Authentication authentication
    ) {
        ReservationResponseDTO response = reservationService.cancelReservation(
                id, authentication.getName());
        return responseBuilder.buildResponse(
                "Reservation cancelled successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Quitar un asiento de la reserva",
            description = "Libera el asiento indicado (vuelve a AVAILABLE) y recalcula los montos de la reserva. " +
                    "Si era el último asiento, cancela la reserva completa. Solo aplica a reservas en estado PENDING y del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento removido exitosamente (o reserva cancelada si era el último)",
                    content = @Content(schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "La reserva no está en estado PENDING"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada, no pertenece al usuario, o el asiento no pertenece a la reserva")
    })
    // DELETE — removes a single seat from a PENDING reservation; auto-cancels if last seat
    @DeleteMapping("/{reservationId}/seats/{reservationSeatId}")
    public ResponseEntity<GeneralResponse> removeSeatFromReservation(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Integer reservationId,
            @Parameter(description = "ID de la línea ReservationSeat a remover", example = "3")
            @PathVariable Integer reservationSeatId,
            Authentication authentication
    ) {
        ReservationResponseDTO response = reservationService.removeSeatFromReservation(
                reservationId, reservationSeatId, authentication.getName());
        return responseBuilder.buildResponse(
                "Seat removed from reservation successfully", HttpStatus.OK, response);
    }
}
