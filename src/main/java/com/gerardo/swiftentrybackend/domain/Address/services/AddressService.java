package com.gerardo.swiftentrybackend.domain.Address.services;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.dto.response.AddressResponseDTO;

import java.util.List;

// Operaciones de negocio disponibles para el dominio Address
public interface AddressService {
    // Crea una dirección a partir del DTO de entrada
    AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO);

    // Obtiene una dirección por id o lanza ResourceNotFoundException
    AddressResponseDTO getAddress(Integer addressId);

    // Lista todas las direcciones existentes
    List<AddressResponseDTO> getAllAddresses();
}
