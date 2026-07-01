package com.gerardo.swiftentrybackend.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

// Envoltorio estándar de respuesta de la API (uri, mensaje, status, data)
@Data
@Builder
@Schema(description = "Envoltorio estándar de todas las respuestas de la API.")
public class GeneralResponse {
    @Schema(description = "Ruta del endpoint invocado", example = "/swift_entry/events")
    private String uri;
    @Schema(description = "Mensaje descriptivo del resultado de la operación", example = "Event retrieved successfully")
    private String message;
    @Schema(description = "Código de estado HTTP de la respuesta", example = "200")
    private int status;
    @Schema(description = "Marca de tiempo en que se generó la respuesta")
    private LocalDateTime time;
    @Schema(description = "Cuerpo de datos de la respuesta; su forma depende del endpoint (objeto, lista o null)")
    private Object data;
}
