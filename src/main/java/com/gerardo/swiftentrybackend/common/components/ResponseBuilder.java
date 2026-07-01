package com.gerardo.swiftentrybackend.common.components;

import com.gerardo.swiftentrybackend.common.dto.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// Construye el envoltorio de respuesta estándar (GeneralResponse) usado por todos los controladores
@Component("apiResponseBuilder")
public class ResponseBuilder {
    // Arma un ResponseEntity con uri, mensaje, status y data dentro de GeneralResponse
    public ResponseEntity<GeneralResponse> buildResponse(
            String message,
            HttpStatus status,
            Object data
    ) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .getPath();


        return ResponseEntity.status(status)
                .body(
                        GeneralResponse.builder()
                                .uri(uri)
                                .message(message)
                                .status(status.value())
                                .data(data)
                                .build()
                );
    }
}
