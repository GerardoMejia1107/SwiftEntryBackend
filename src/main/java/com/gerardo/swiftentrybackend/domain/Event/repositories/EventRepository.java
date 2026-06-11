package com.gerardo.swiftentrybackend.domain.Event.repositories;

import com.gerardo.swiftentrybackend.domain.Event.EventModel;
import jdk.jfr.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventModel, Integer> {
    boolean existsByName(String name);
}
