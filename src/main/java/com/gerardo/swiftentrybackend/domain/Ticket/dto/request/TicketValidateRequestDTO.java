package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketValidateRequestDTO {

    @NotBlank(message = "QR code is required")
    private String qrCode;
}
