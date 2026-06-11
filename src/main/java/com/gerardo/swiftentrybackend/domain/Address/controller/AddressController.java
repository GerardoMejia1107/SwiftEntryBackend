package com.gerardo.swiftentrybackend.domain.Address.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.dto.response.AddressResponseDTO;
import com.gerardo.swiftentrybackend.domain.Address.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/swift_entry/addresses")
public class AddressController {
    private final AddressService addressService;
    private final ResponseBuilder responseBuilder;

    @PostMapping
    public ResponseEntity<GeneralResponse> createAddress(@Valid @RequestBody AddressRequestDTO requestDTO) {
        AddressResponseDTO response = addressService.createAddress(requestDTO);
        return responseBuilder.buildResponse(
                "Address created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> getAddress(@Valid @RequestBody AddressRequestDTO requestDTO) {
        List<AddressResponseDTO> response = addressService.getAllAddresses();
        return responseBuilder.buildResponse(
                "Address retrieved successfully",
                HttpStatus.OK,
                response
        );
    }
}
