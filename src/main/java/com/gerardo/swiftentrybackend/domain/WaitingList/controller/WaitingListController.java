package com.gerardo.swiftentrybackend.domain.WaitingList.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.request.WaitingListRequestDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.response.WaitingListResponseDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.service.WaitingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swift_entry/waiting-list")
@RequiredArgsConstructor
// Expone los endpoints para unirse, salir y consultar la lista de espera de una localidad sin cupo
public class WaitingListController {

    private final WaitingListService waitingListService;
    private final ResponseBuilder responseBuilder;

    // Une al usuario autenticado a la lista de espera de una localidad
    @PostMapping
    public ResponseEntity<GeneralResponse> joinWaitingList(
            @Valid @RequestBody WaitingListRequestDTO request,
            Authentication authentication
    ) {
        WaitingListResponseDTO response = waitingListService.joinWaitingList(
                request, authentication.getName());
        return responseBuilder.buildResponse(
                "Successfully joined the waiting list", HttpStatus.CREATED, response);
    }

    // Cancela la entrada del usuario autenticado en la lista de espera
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> leaveWaitingList(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        WaitingListResponseDTO response = waitingListService.leaveWaitingList(
                id, authentication.getName());
        return responseBuilder.buildResponse(
                "Successfully left the waiting list", HttpStatus.OK, response);
    }

    // Lista todas las entradas de lista de espera del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyEntries(Authentication authentication) {
        List<WaitingListResponseDTO> response = waitingListService.getMyEntries(
                authentication.getName());
        return responseBuilder.buildResponse(
                "Waiting list entries retrieved", HttpStatus.OK, response);
    }

    // Lista todas las entradas de lista de espera del sistema (solo administradores)
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getAllEntries() {
        List<WaitingListResponseDTO> response = waitingListService.getAllEntries();
        return responseBuilder.buildResponse(
                "All waiting list entries retrieved", HttpStatus.OK, response);
    }

    // Lista las entradas de lista de espera de una localidad específica (solo administradores)
    @GetMapping("/locality/{localityId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<GeneralResponse> getEntriesByLocality(@PathVariable Long localityId) {
        List<WaitingListResponseDTO> response = waitingListService.getEntriesByLocality(localityId);
        return responseBuilder.buildResponse(
                "Waiting list entries for locality retrieved", HttpStatus.OK, response);
    }
}
