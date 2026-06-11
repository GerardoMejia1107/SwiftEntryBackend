package com.gerardo.swiftentrybackend.domain.Event.utils;

import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import jakarta.persistence.Column;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToStatusConverter implements Converter<String, EventStatus> {
    @Override
    public EventStatus convert(String source) {
        return EventStatus.valueOf(source.toUpperCase());
    }
}
