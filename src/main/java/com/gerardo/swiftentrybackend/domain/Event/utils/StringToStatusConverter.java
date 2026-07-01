package com.gerardo.swiftentrybackend.domain.Event.utils;

import com.gerardo.swiftentrybackend.domain.Event.enums.EventStatus;
import jakarta.persistence.Column;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
// Convierte el parámetro de query string a EventStatus para su uso en filtros de endpoints.
public class StringToStatusConverter implements Converter<String, EventStatus> {
    // Convierte el texto (case-insensitive) al valor de enum correspondiente.
    @Override
    public EventStatus convert(String source) {
        return EventStatus.valueOf(source.toUpperCase());
    }
}
