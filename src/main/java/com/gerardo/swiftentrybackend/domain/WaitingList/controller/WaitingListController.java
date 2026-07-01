package com.gerardo.swiftentrybackend.domain.WaitingList.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.request.WaitingListRequestDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.response.WaitingListResponseDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.service.WaitingListService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Lista de espera", description = "Registro y consulta de la lista de espera para localidades sin cupo disponible")
@RestController
@RequestMapping("/swift_entry/waiting-list")
@RequiredArgsConstructor
// Expone los endpoints para unirse, salir y consultar la lista de espera de una localidad sin cupo
public class WaitingListController {

    private final WaitingListService waitingListService;
    private final ResponseBuilder responseBuilder;

    // Une al usuario autenticado a la lista de espera de una localidad
    @Operation(summary = "Unirse a la lista de espera",
            description = "Registra al usuario autenticado en la lista de espera de una localidad sin cupo disponible. Falla si la localidad todavía tiene cupo, o si el usuario ya tiene una entrada activa (WAITING o NOTIFIED) para esa localidad. " +
                    "Cuando se libera un cupo, WaitingListScheduler notifica automáticamente (cada 60s revisa expiraciones) al siguiente usuario en la cola por orden de antigüedad; el usuario notificado tiene una ventana de 30 minutos para reservar antes de perder su turno y que se notifique al siguiente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario agregado a la lista de espera exitosamente",
                    content = @Content(schema = @Schema(implementation = WaitingListResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "La localidad todavía tiene cupo disponible; debe reservar directamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario o localidad no encontrados"),
            @ApiResponse(responseCode = "409", description = "El usuario ya tiene una entrada activa en la lista de espera para esa localidad")
    })
    @PostMapping
    public ResponseEntity<GeneralResponse> joinWaitingList(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Localidad a la que desea unirse a la lista de espera", required = true)
            WaitingListRequestDTO request,
            Authentication authentication
    ) {
        WaitingListResponseDTO response = waitingListService.joinWaitingList(
                request, authentication.getName());
        return responseBuilder.buildResponse(
                "Successfully joined the waiting list", HttpStatus.CREATED, response);
    }

    // Cancela la entrada del usuario autenticado en la lista de espera
    @Operation(summary = "Salir de la lista de espera",
            description = "Cancela la entrada del usuario autenticado en la lista de espera. Solo el dueño de la entrada puede cancelarla, y solo si está en estado WAITING o NOTIFIED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada cancelada exitosamente",
                    content = @Content(schema = @Schema(implementation = WaitingListResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "La entrada no pertenece al usuario autenticado, o no está en estado WAITING/NOTIFIED"),
            @ApiResponse(responseCode = "404", description = "Usuario o entrada de lista de espera no encontrados")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> leaveWaitingList(
            @Parameter(description = "ID de la entrada de lista de espera", example = "1")
            @PathVariable Integer id,
            Authentication authentication
    ) {
        WaitingListResponseDTO response = waitingListService.leaveWaitingList(
                id, authentication.getName());
        return responseBuilder.buildResponse(
                "Successfully left the waiting list", HttpStatus.OK, response);
    }

    // Lista todas las entradas de lista de espera del usuario autenticado
    @Operation(summary = "Mis entradas en lista de espera", description = "Lista todas las entradas de lista de espera (de cualquier estado) del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entradas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = WaitingListResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyEntries(Authentication authentication) {
        List<WaitingListResponseDTO> response = waitingListService.getMyEntries(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Waiting list entries retrieved", HttpStatus.OK, response);
    }

    // Lista todas las entradas de lista de espera del sistema (solo administradores)
    @Operation(summary = "Listar todas las entradas", description = "Requiere rol ADMINISTRATOR. Devuelve todas las entradas de lista de espera del sistema, de cualquier localidad y estado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entradas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = WaitingListResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getAllEntries() {
        List<WaitingListResponseDTO> response = waitingListService.getAllEntries();
        return responseBuilder.buildResponse(
                "All waiting list entries retrieved", HttpStatus.OK, response);
    }

    // Lista las entradas de lista de espera de una localidad específica (solo administradores)
    @Operation(summary = "Listar entradas por localidad", description = "Requiere rol ADMINISTRATOR. Devuelve las entradas de lista de espera de una localidad específica, de cualquier estado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entradas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = WaitingListResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMINISTRATOR"),
            @ApiResponse(responseCode = "404", description = "Localidad no encontrada")
    })
    @GetMapping("/locality/{localityId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getEntriesByLocality(
            @Parameter(description = "ID de la localidad", example = "2")
            @PathVariable Long localityId) {
        List<WaitingListResponseDTO> response = waitingListService.getEntriesByLocality(localityId);
        return responseBuilder.buildResponse(
                "Waiting list entries for locality retrieved", HttpStatus.OK, response);
    }
}
