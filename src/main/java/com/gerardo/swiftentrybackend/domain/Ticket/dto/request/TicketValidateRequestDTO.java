package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// Datos para validar un ticket en el escaneo de acceso: el código QR leído.
@Schema(description = "Datos para validar un ticket en el escaneo de acceso: el código QR leído")
public class TicketValidateRequestDTO {

    @Schema(description = "Código QR leído del ticket en el punto de acceso", example = "QR-3f2a9c1e-4b5d-4e6f-8a9b-1234567890ab")
    @NotBlank(message = "QR code is required")
    private String qrCode;
}
