package com.gerardo.swiftentrybackend.domain.Address.repositories;

import com.gerardo.swiftentrybackend.domain.Address.model.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressModel, Integer> {
}
