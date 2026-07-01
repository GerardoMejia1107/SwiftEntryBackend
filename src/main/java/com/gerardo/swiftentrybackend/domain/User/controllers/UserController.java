package com.gerardo.swiftentrybackend.domain.User.controllers;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.User.dto.request.UserRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.dto.response.UserResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.services.UserService;
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

// Endpoints REST para registro y consulta de usuarios
@Tag(name = "Usuarios", description = "Registro y consulta de usuarios")
@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/users")
public class UserController {
    private final UserService userService;
    private final ResponseBuilder responseBuilder;

    // Registra un usuario nuevo (ruta pública)
    @Operation(summary = "Registrar usuario", description = "Ruta pública. Crea una cuenta nueva. No requiere autenticación.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (validación de campos)"),
            @ApiResponse(responseCode = "404", description = "El rol indicado (roleId) no existe"),
            @ApiResponse(responseCode = "409", description = "Ya existe un usuario registrado con ese email")
    })
    @PostMapping
    public ResponseEntity<GeneralResponse> createUser(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo usuario, incluye su dirección anidada", required = true)
            UserRequestDTO requestDTO) {
        UserResponseDTO response = userService.createUser(requestDTO);
        return responseBuilder.buildResponse(
                "User created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    // Lista todos los usuarios (requiere rol ADMINISTRATOR)
    @Operation(summary = "Listar todos los usuarios", description = "Requiere rol ADMINISTRATOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMINISTRATOR")
    })
    @GetMapping
    public ResponseEntity<GeneralResponse> getUsers() {
        List<UserResponseDTO> response = userService.getAllUsers();
        return responseBuilder.buildResponse(
                "Users found successfully",
                HttpStatus.OK,
                response
        );
    }

    // Obtiene un usuario por id (dueño del perfil o ADMINISTRATOR)
    @Operation(summary = "Obtener usuario por ID",
            description = "Requiere autenticación. Un usuario solo puede ver su propio perfil; ADMINISTRATOR puede ver cualquiera. La verificación de propiedad se realiza en el servicio.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es el dueño del perfil ni ADMINISTRATOR"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getUserById(
            @Valid @PathVariable
            @Parameter(description = "ID del usuario a consultar", example = "1")
            Integer id) {
        UserResponseDTO response = userService.getUserById(id);
        return responseBuilder.buildResponse(
                "User found successfully",
                HttpStatus.OK,
                response
        );
    }

}
