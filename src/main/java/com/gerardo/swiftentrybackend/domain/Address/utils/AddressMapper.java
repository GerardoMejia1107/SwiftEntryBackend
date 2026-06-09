package com.gerardo.swiftentrybackend.domain.Address.utils;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.dto.response.AddressResponseDTO;
import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressModel toModel(AddressRequestDTO request) {
        return AddressModel.builder()
                .streetAddress(request.getStreetAddress())
                .neighborhood(request.getNeighborhood())
                .municipality(request.getMunicipality())
                .department(request.getDepartment())
                .country(request.getCountry())
                .referencePoint(request.getReferencePoint())
                .build();
    }

    public AddressResponseDTO toResponse(AddressModel address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .streetAddress(address.getStreetAddress())
                .neighborhood(address.getNeighborhood())
                .municipality(address.getMunicipality())
                .department(address.getDepartment())
                .country(address.getCountry())
                .referencePoint(address.getReferencePoint())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}