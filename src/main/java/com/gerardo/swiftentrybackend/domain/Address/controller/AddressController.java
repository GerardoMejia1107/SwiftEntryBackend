package com.gerardo.swiftentrybackend.domain.Address.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.dto.response.AddressResponseDTO;
import com.gerardo.swiftentrybackend.domain.Address.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints REST para creación y consulta de direcciones postales
@Tag(name = "Direcciones", description = "Gestión de direcciones postales de usuarios y eventos")
@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/addresses")
public class AddressController {
    private final AddressService addressService;
    private final ResponseBuilder responseBuilder;

    // Crea una nueva dirección
    @Operation(summary = "Crear dirección", description = "Requiere autenticación.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dirección creada exitosamente",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (validación de campos)"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<GeneralResponse> createAddress(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nueva dirección", required = true)
            AddressRequestDTO requestDTO) {
        AddressResponseDTO response = addressService.createAddress(requestDTO);
        return responseBuilder.buildResponse(
                "Address created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    // Lista todas las direcciones registradas
    @Operation(summary = "Listar todas las direcciones",
            description = "Requiere autenticación. PENDIENTE: revisar — este endpoint ignora el body recibido y siempre retorna todas las direcciones, sin filtrar ni usar los datos enviados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Direcciones recuperadas exitosamente",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<GeneralResponse> getAddress(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "PENDIENTE: revisar — este body se recibe pero el método lo ignora por completo; no afecta el resultado.", required = true)
            AddressRequestDTO requestDTO) {
        List<AddressResponseDTO> response = addressService.getAllAddresses();
        return responseBuilder.buildResponse(
                "Address retrieved successfully",
                HttpStatus.OK,
                response
        );
    }
}
