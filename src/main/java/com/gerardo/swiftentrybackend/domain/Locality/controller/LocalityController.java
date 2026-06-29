package com.gerardo.swiftentrybackend.domain.Locality.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import com.gerardo.swiftentrybackend.domain.Locality.services.LocalityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Localidades", description = "Secciones con precio dentro de un evento (VIP, General, etc.)")
@RestController
@RequestMapping("/swift_entry/localities")
@RequiredArgsConstructor
public class LocalityController {

    private final LocalityService localityService;
    private final ResponseBuilder responseBuilder;

    @Operation(summary = "Listar todas las localidades", description = "Ruta pública")
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllLocalities() {
        List<LocalityResponseDTO> response = localityService.getAllLocalities();
        return responseBuilder.buildResponse("Localities retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Obtener localidad por ID")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getLocalityById(@PathVariable Long id) {
        LocalityResponseDTO response = localityService.getLocalityById(id);
        return responseBuilder.buildResponse("Locality found successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Localidades de un evento")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> getLocalitiesByEventId(@PathVariable Integer eventId) {
        List<LocalityResponseDTO> response = localityService.getLocalitiesByEventId(eventId);
        return responseBuilder.buildResponse("Localities retrieved successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Actualizar localidad")
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateLocality(
            @PathVariable Long id,
            @Valid @RequestBody LocalityUpdateDTO request
    ) {
        LocalityResponseDTO response = localityService.updateLocality(id, request);
        return responseBuilder.buildResponse("Locality updated successfully", HttpStatus.OK, response);
    }

    @Operation(summary = "Eliminar localidad")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteLocality(@PathVariable Long id) {
        localityService.deleteLocality(id);
        return responseBuilder.buildResponse("Locality deleted successfully", HttpStatus.OK, null);
    }
}
