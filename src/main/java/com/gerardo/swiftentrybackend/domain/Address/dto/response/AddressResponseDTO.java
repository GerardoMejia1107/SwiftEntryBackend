package com.gerardo.swiftentrybackend.domain.Address.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

    private Integer id;
    private String streetAddress;
    private String neighborhood;
    private String municipality;
    private String department;
    private String country;
    private String referencePoint;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}