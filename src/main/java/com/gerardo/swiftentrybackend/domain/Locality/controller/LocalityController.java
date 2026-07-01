package com.gerardo.swiftentrybackend.domain.Locality.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import com.gerardo.swiftentrybackend.domain.Locality.services.LocalityService;
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

@Tag(name = "Localidades", description = "Secciones con precio dentro de un evento (VIP, General, etc.)")
@RestController
@RequestMapping("/swift_entry/localities")
@RequiredArgsConstructor
// Controlador REST de localidades: consulta, actualización y eliminación (la creación va vía Event).
public class LocalityController {

    private final LocalityService localityService;
    private final ResponseBuilder responseBuilder;

    // Lista todas las localidades; ruta pública.
    @Operation(summary = "Listar todas las localidades", description = "Ruta pública")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localidades recuperadas exitosamente",
                    content = @Content(schema = @Schema(implementation = LocalityResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllLocalities() {
        List<LocalityResponseDTO> response = localityService.getAllLocalities();
        return responseBuilder.buildResponse("Localities retrieved successfully", HttpStatus.OK, response);
    }

    // Obtiene una localidad por id.
    @Operation(summary = "Obtener localidad por ID", description = "Ruta pública")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localidad encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = LocalityResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe una localidad con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getLocalityById(
            @Parameter(description = "ID de la localidad", example = "1") @PathVariable Long id) {
        LocalityResponseDTO response = localityService.getLocalityById(id);
        return responseBuilder.buildResponse("Locality found successfully", HttpStatus.OK, response);
    }

    // Lista las localidades pertenecientes a un evento.
    @Operation(summary = "Localidades de un evento", description = "Ruta pública. Lista las localidades pertenecientes al evento indicado")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localidades recuperadas exitosamente",
                    content = @Content(schema = @Schema(implementation = LocalityResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un evento con el ID indicado")
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> getLocalitiesByEventId(
            @Parameter(description = "ID del evento", example = "1") @PathVariable Integer eventId) {
        List<LocalityResponseDTO> response = localityService.getLocalitiesByEventId(eventId);
        return responseBuilder.buildResponse("Localities retrieved successfully", HttpStatus.OK, response);
    }

    // Actualiza parcialmente una localidad existente.
    @Operation(summary = "Actualizar localidad", description = "Ruta autenticada. Actualiza únicamente los campos no nulos enviados en el request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localidad actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = LocalityResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (validación de campos)"),
            @ApiResponse(responseCode = "404", description = "No existe una localidad con el ID indicado"),
            @ApiResponse(responseCode = "409", description = "Conflicto de bloqueo optimista (@Version); reintentar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateLocality(
            @Parameter(description = "ID de la localidad", example = "1") @PathVariable Long id,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Campos a actualizar de la localidad (los nulos se ignoran)", required = true)
            LocalityUpdateDTO request
    ) {
        LocalityResponseDTO response = localityService.updateLocality(id, request);
        return responseBuilder.buildResponse("Locality updated successfully", HttpStatus.OK, response);
    }

    // Elimina una localidad; falla si ya tiene reservas asociadas.
    @Operation(summary = "Eliminar localidad", description = "Ruta autenticada. Elimina la localidad junto con sus LocalitySeat asociados; " +
            "rechaza el borrado si ya tiene reservas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Localidad eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "No se puede eliminar la localidad porque ya tiene reservas asociadas"),
            @ApiResponse(responseCode = "404", description = "No existe una localidad con el ID indicado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteLocality(
            @Parameter(description = "ID de la localidad", example = "1") @PathVariable Long id) {
        localityService.deleteLocality(id);
        return responseBuilder.buildResponse("Locality deleted successfully", HttpStatus.OK, null);
    }
}
