package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// Datos para ceder un ticket a otro usuario, identificado por su email.
@Schema(description = "Datos para ceder un ticket a otro usuario, identificado por su email")
public class TicketTransferRequestDTO {

    @Schema(description = "Email del usuario registrado que recibirá el ticket", example = "receptor@example.com")
    @NotBlank(message = "Receiver email is required")
    @Email(message = "Receiver email must be a valid email address")
    private String receiverEmail;
}
