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

    public void updateModel(AddressModel address, AddressRequestDTO request) {
        if (request.getStreetAddress() != null) address.setStreetAddress(request.getStreetAddress());
        if (request.getNeighborhood() != null) address.setNeighborhood(request.getNeighborhood());
        if (request.getMunicipality() != null) address.setMunicipality(request.getMunicipality());
        if (request.getDepartment() != null) address.setDepartment(request.getDepartment());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getReferencePoint() != null) address.setReferencePoint(request.getReferencePoint());
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