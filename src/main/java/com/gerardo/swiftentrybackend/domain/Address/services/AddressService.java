package com.gerardo.swiftentrybackend.domain.Address.services;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.dto.response.AddressResponseDTO;

import java.util.List;

public interface AddressService {
    AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO);

    AddressResponseDTO getAddress(Integer addressId);

    List<AddressResponseDTO> getAllAddresses();
}
