package com.gerardo.swiftentrybackend.domain.Ticket.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketTransferRequestDTO {

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Receiver email must be a valid email address")
    private String receiverEmail;
}
