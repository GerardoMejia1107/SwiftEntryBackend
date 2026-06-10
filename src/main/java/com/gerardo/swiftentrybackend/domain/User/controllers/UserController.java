package com.gerardo.swiftentrybackend.domain.User.controllers;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.User.dto.request.UserRequestDTO;
import com.gerardo.swiftentrybackend.domain.User.dto.response.UserResponseDTO;
import com.gerardo.swiftentrybackend.domain.User.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/users")
public class UserController {
    private final UserService userService;
    private final ResponseBuilder responseBuilder;

    @PostMapping
    public ResponseEntity<GeneralResponse> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO response = userService.createUser(requestDTO);
        return responseBuilder.buildResponse(
                "User created successfully",
                HttpStatus.CREATED,
                response
        );
    }

}
