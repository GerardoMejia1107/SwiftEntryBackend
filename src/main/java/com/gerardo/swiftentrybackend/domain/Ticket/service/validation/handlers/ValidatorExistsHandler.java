package com.gerardo.swiftentrybackend.domain.Ticket.service.validation.handlers;

import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationContext;
import com.gerardo.swiftentrybackend.domain.Ticket.service.validation.TicketValidationHandler;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Tercer paso: busca al usuario que está escaneando el ticket y lo carga en el contexto.
@Component
@RequiredArgsConstructor
public class ValidatorExistsHandler extends TicketValidationHandler {

    private final UserRepository userRepository;

    @Override
    protected void process(TicketValidationContext context) {

        UserModel validator = userRepository
                .findByEmail(context.getValidatorEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario no encontrado: "
                                        + context.getValidatorEmail()));

        context.setValidator(validator);
    }
}
