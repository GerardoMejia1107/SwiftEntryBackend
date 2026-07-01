package com.gerardo.swiftentrybackend.domain.Address.repositories;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de AddressModel; solo usa métodos heredados de JpaRepository
public interface AddressRepository extends JpaRepository<AddressModel, Integer> {
}
