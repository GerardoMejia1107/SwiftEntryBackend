package com.gerardo.swiftentrybackend.domain.User.controllers;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.User.dto.request.UserRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.dto.response.UserResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Registro y consulta de usuarios")
@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/users")
public class UserController {
    private final UserService userService;
    private final ResponseBuilder responseBuilder;

    @Operation(summary = "Registrar usuario", description = "Ruta pública. Crea una cuenta nueva con rol por defecto")
    @PostMapping
    public ResponseEntity<GeneralResponse> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO response = userService.createUser(requestDTO);
        return responseBuilder.buildResponse(
                "User created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    @Operation(summary = "Listar todos los usuarios", description = "Solo ADMINISTRATOR")
    @GetMapping
    public ResponseEntity<GeneralResponse> getUsers() {
        List<UserResponseDTO> response = userService.getAllUsers();
        return responseBuilder.buildResponse(
                "Users found successfully",
                HttpStatus.OK,
                response
        );
    }

    @Operation(summary = "Obtener usuario por ID", description = "Un usuario solo puede ver su propio perfil; ADMINISTRATOR puede ver cualquiera")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getUserById(@Valid @PathVariable Integer id) {
        UserResponseDTO response = userService.getUserById(id);
        return responseBuilder.buildResponse(
                "User found successfully",
                HttpStatus.OK,
                response
        );
    }

}
