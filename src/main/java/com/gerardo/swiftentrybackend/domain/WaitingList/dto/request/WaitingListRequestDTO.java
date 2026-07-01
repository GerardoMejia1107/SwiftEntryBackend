package com.gerardo.swiftentrybackend.domain.WaitingList.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos requeridos para unirse a la lista de espera de una localidad")
// Datos requeridos para unirse a la lista de espera de una localidad
public class WaitingListRequestDTO {

    @NotNull(message = "Locality ID is required")
    @Schema(description = "ID de la localidad sin cupo a la que el usuario desea unirse en lista de espera", example = "2")
    private Long localityId;
}
