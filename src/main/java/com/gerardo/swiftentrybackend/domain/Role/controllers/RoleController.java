package com.gerardo.swiftentrybackend.domain.Role.controllers;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;

import com.gerardo.swiftentrybackend.domain.Role.services.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints REST para creación y consulta de roles de usuario
@Tag(name = "Roles", description = "Gestión de roles de usuario")
@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/roles")
public class RoleController {
    private final RoleService roleService;
    private final ResponseBuilder responseBuilder;

    // Crea un nuevo rol
    @Operation(summary = "Crear rol")
    @PostMapping
    public ResponseEntity<GeneralResponse> creatRole(
            @Valid @RequestBody RoleRequestDTO requestDTO) {
        RoleResponseDTO response = roleService.createRole(requestDTO);
        return responseBuilder.buildResponse(
                "Role created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    // Lista todos los roles (requiere rol ADMINISTRATOR)
    @Operation(summary = "Listar todos los roles", description = "Solo ADMINISTRATOR")
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllRoles() {
        List<RoleResponseDTO> response = roleService.getAllRoles();

        return responseBuilder.buildResponse(
                "Roles retrieved successfully",
                HttpStatus.OK,
                response
        );
    }

}
