package com.gerardo.swiftentrybackend.domain.Locality.repositories;

import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalityRepository extends JpaRepository<LocalityModel, Long> {
    boolean existsByNameAndEvent_Id(String name, Integer eventId);
    List<LocalityModel> findAllByEvent_Id(Integer eventId);
}
