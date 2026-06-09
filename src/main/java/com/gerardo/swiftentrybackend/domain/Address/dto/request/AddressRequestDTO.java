package com.gerardo.swiftentrybackend.domain.Address.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {

    @NotBlank
    @Size(max = 255)
    private String streetAddress;

    @Size(max = 100)
    private String neighborhood;

    @NotBlank
    @Size(max = 100)
    private String municipality;

    @NotBlank
    @Size(max = 100)
    private String department;

    @NotBlank
    @Size(max = 100)
    private String country;

    @Size(max = 255)
    private String referencePoint;
}