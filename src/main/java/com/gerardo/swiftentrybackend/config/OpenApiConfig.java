package com.gerardo.swiftentrybackend.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI swiftEntryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SwiftEntry API")
                        .version("1.0.0")
                        .description("""
                                Backend REST de SwiftEntry — plataforma de venta de boletos para eventos.

                                **Autenticación:** todas las rutas protegidas requieren un JWT en el header \
                                `Authorization: Bearer <token>`. Obtén el token en `POST /swift_entry/auth/login` \
                                y pégalo en el botón **Authorize** de arriba.

                                **Flujo principal:** Evento → Localidad → Asiento → Reserva → Pago → Boleto
                                """)
                        .contact(new Contact()
                                .name("SwiftEntry Team")
                                .email("gerardo.port1107.psql@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
