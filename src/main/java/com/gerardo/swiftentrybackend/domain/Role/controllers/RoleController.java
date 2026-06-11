package com.gerardo.swiftentrybackend.domain.Role.controllers;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Role.dto.request.RoleRequestDTO;
import com.gerardo.swiftentrybackend.domain.Role.dto.response.RoleResponseDTO;

import com.gerardo.swiftentrybackend.domain.Role.services.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/roles")
public class RoleController {
    private final RoleService roleService;
    private final ResponseBuilder responseBuilder;

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
