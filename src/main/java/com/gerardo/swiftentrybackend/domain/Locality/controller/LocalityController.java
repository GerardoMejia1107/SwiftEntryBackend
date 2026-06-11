package com.gerardo.swiftentrybackend.domain.Locality.controller;

import com.gerardo.swiftentrybackend.common.components.ResponseBuilder;
import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityRequestDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.request.LocalityUpdateDTO;
import com.gerardo.swiftentrybackend.domain.Locality.dto.response.LocalityResponseDTO;
import com.gerardo.swiftentrybackend.domain.Locality.services.LocalityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swift_entry/localities")
@RequiredArgsConstructor
public class LocalityController {

    private final LocalityService localityService;
    private final ResponseBuilder responseBuilder;

    @PostMapping
    public ResponseEntity<GeneralResponse> createLocality(@Valid @RequestBody LocalityRequestDTO request) {
        LocalityResponseDTO response = localityService.createLocality(request);
        return responseBuilder.buildResponse("Locality created successfully", HttpStatus.CREATED, response);
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> getAllLocalities() {
        List<LocalityResponseDTO> response = localityService.getAllLocalities();
        return responseBuilder.buildResponse("Localities retrieved successfully", HttpStatus.OK, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getLocalityById(@PathVariable Long id) {
        LocalityResponseDTO response = localityService.getLocalityById(id);
        return responseBuilder.buildResponse("Locality found successfully", HttpStatus.OK, response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> getLocalitiesByEventId(@PathVariable Integer eventId) {
        List<LocalityResponseDTO> response = localityService.getLocalitiesByEventId(eventId);
        return responseBuilder.buildResponse("Localities retrieved successfully", HttpStatus.OK, response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateLocality(
            @PathVariable Long id,
            @Valid @RequestBody LocalityUpdateDTO request
    ) {
        LocalityResponseDTO response = localityService.updateLocality(id, request);
        return responseBuilder.buildResponse("Locality updated successfully", HttpStatus.OK, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteLocality(@PathVariable Long id) {
        localityService.deleteLocality(id);
        return responseBuilder.buildResponse("Locality deleted successfully", HttpStatus.OK, null);
    }
}
