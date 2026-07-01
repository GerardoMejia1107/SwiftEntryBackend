package com.gerardo.swiftentrybackend.domain.Address.services;

import com.gerardo.swiftentrybackend.domain.Address.dto.request.AddressRequestDTO;
import com.gerardo.swiftentrybackend.domain.Address.dto.response.AddressResponseDTO;
import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import com.gerardo.swiftentrybackend.domain.Address.repositories.AddressRepository;
import com.gerardo.swiftentrybackend.domain.Address.utils.AddressMapper;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementación de AddressService respaldada por AddressRepository
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    // Persiste una nueva dirección
    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO) {

        AddressModel address = addressMapper.toModel(addressRequestDTO);

        return addressMapper.toResponse(
                addressRepository.save(address)
        );
    }

    // Busca una dirección por id; lanza ResourceNotFoundException si no existe
    @Override
    public AddressResponseDTO getAddress(Integer addressId) {

        AddressModel address = addressRepository.findById(addressId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Address with id " + addressId + " not found"
                        )
                );

        return addressMapper.toResponse(address);
    }

    // Devuelve todas las direcciones mapeadas a DTO
    @Override
    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }


}
