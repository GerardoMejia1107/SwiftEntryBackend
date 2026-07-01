package com.gerardo.swiftentrybackend.domain.Seat.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Seat.dto.request.SeatAssignmentRequestDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.LocalitySeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatMapResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.dto.response.SeatResponseDTO;
import com.gerardo.swiftentrybackend.domain.Seat.services.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
    @Operation(summary = "Inicializar grilla de asientos", description = "Ruta autenticada. Crea todos los asientos físicos del venue " +
            "(grilla fija de filas A-J x 16 columnas) que aún no existan. Llamar una sola vez por venue; es idempotente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grilla de asientos inicializada exitosamente")
    })
    @PostMapping("/initialize")
    public ResponseEntity<GeneralResponse> initializeSeats() {
        seatService.initializeSeats();
        return responseBuilder.buildResponse("Seat grid initialized successfully", HttpStatus.CREATED, null);
    }

    // Vincula asientos físicos a una localidad, creando los LocalitySeat en estado AVAILABLE.
    @Operation(summary = "Asignar asientos a una localidad", description = "Ruta autenticada. Vincula asientos físicos (identificados como fila+columna, " +
            "ej. \"A1\") a una localidad; crea los registros en locality_seats con estado AVAILABLE e incrementa capacity/availableSlots de la localidad")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asientos asignados exitosamente",
                    content = @Content(schema = @Schema(implementation = LocalitySeatResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (validación de campos)"),
            @ApiResponse(responseCode = "404", description = "No existe la localidad, o alguno de los asientos no ha sido inicializado"),
            @ApiResponse(responseCode = "409", description = "Identificador de asiento con formato inválido, o el asiento ya está asignado a una localidad de ese evento")
    })
    @PostMapping("/assign")
    public ResponseEntity<GeneralResponse> assignSeats(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Localidad destino y lista de identificadores de asientos (ej. [\"A1\", \"A2\"])", required = true)
            SeatAssignmentRequestDTO request) {
        List<LocalitySeatResponseDTO> response = seatService.assignSeats(request);
        return responseBuilder.buildResponse("Seats assigned successfully", HttpStatus.CREATED, response);
    }

    // Devuelve el mapa de asientos del evento con su estado actual.
    @Operation(summary = "Mapa de asientos de un evento", description = "Ruta pública. Devuelve todos los asientos físicos del venue, indicando para " +
            "cada uno su asignación (si existe) dentro del evento y su estado actual (AVAILABLE/RESERVED/SOLD), ordenados por fila y columna")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mapa de asientos recuperado exitosamente",
                    content = @Content(schema = @Schema(implementation = SeatMapResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un evento con el ID indicado")
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> getSeatMapByEventId(
            @Parameter(description = "ID del evento", example = "1") @PathVariable Integer eventId) {
        List<SeatMapResponseDTO> response = seatService.getSeatMapByEventId(eventId);
        return responseBuilder.buildResponse("Seat map retrieved successfully", HttpStatus.OK, response);
    }

    // Lista todos los asientos físicos.
    @Operation(summary = "Listar todos los asientos", description = "Ruta pública. Lista todos los asientos físicos de la grilla del venue")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asientos recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = SeatResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllSeats() {
        List<SeatResponseDTO> response = seatService.getAllSeats();
        return responseBuilder.buildResponse("Seats retrieved successfully", HttpStatus.OK, response);
    }

    // Obtiene un asiento físico por id.
    @Operation(summary = "Obtener asiento por ID", description = "Ruta pública")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = SeatResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un asiento con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getSeatById(
            @Parameter(description = "ID del asiento físico", example = "1") @PathVariable Long id) {
        SeatResponseDTO response = seatService.getSeatById(id);
        return responseBuilder.buildResponse("Seat found successfully", HttpStatus.OK, response);
    }

    // Lista los LocalitySeat de una localidad.
    @Operation(summary = "Asientos de una localidad", description = "Ruta pública. Lista los asientos asignados (LocalitySeat) a la localidad indicada")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asientos recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = LocalitySeatResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe una localidad con el ID indicado")
    })
    @GetMapping("/locality/{localityId}")
    public ResponseEntity<GeneralResponse> getSeatsByLocalityId(
            @Parameter(description = "ID de la localidad", example = "1") @PathVariable Long localityId) {
        List<LocalitySeatResponseDTO> response = seatService.getSeatsByLocalityId(localityId);
        return responseBuilder.buildResponse("Seats retrieved successfully", HttpStatus.OK, response);
    }

    // Elimina el vínculo entre un asiento físico y su localidad.
    @Operation(summary = "Desasignar asiento de una localidad", description = "Ruta autenticada. Elimina el LocalitySeat y decrementa " +
            "capacity/availableSlots de la localidad; rechaza la operación si el asiento ya tiene una reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento desasignado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No se puede desasignar el asiento porque tiene una reserva existente"),
            @ApiResponse(responseCode = "404", description = "No existe una asignación LocalitySeat con el ID indicado")
    })
    @DeleteMapping("/assignment/{localitySeatId}")
    public ResponseEntity<GeneralResponse> unassignSeat(
            @Parameter(description = "ID de la asignación LocalitySeat", example = "1") @PathVariable Long localitySeatId) {
        seatService.unassignSeat(localitySeatId);
        return responseBuilder.buildResponse("Seat unassigned successfully", HttpStatus.OK, null);
    }

    // Elimina un asiento físico del venue.
    @Operation(summary = "Eliminar asiento físico", description = "Ruta autenticada. Elimina el asiento físico junto con sus asignaciones a localidades; " +
            "rechaza la operación si ya tiene reservas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asiento eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No se puede eliminar el asiento porque ya tiene reservas asociadas"),
            @ApiResponse(responseCode = "404", description = "No existe un asiento con el ID indicado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteSeat(
            @Parameter(description = "ID del asiento físico", example = "1") @PathVariable Long id) {
        seatService.deleteSeat(id);
        return responseBuilder.buildResponse("Seat deleted successfully", HttpStatus.OK, null);
    }
}
