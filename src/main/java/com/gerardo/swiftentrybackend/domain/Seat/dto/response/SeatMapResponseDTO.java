package com.gerardo.swiftentrybackend.domain.Seat.dto.response;

import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Representa un asiento en el mapa visual del venue para un evento, con su localidad y estado " +
        "(localitySeatId/localityId/localityName/status son null si el asiento aún no ha sido asignado a ninguna localidad del evento)")
// Representa un asiento en el mapa visual del venue para un evento, con su localidad y estado (null si no está asignado).
public class SeatMapResponseDTO {
    @Schema(description = "ID del asiento físico", example = "1")
    private Long seatId;
    @Schema(description = "Etiqueta de la fila del asiento", example = "A")
    private String row;
    @Schema(description = "Número (columna) del asiento dentro de su fila", example = "1")
    private String col;
    @Schema(description = "ID de la asignación LocalitySeat; null si el asiento no está asignado en este evento", example = "1")
    private Long localitySeatId;
    @Schema(description = "ID de la localidad a la que está asignado; null si no está asignado", example = "1")
    private Long localityId;
    @Schema(description = "Nombre de la localidad a la que está asignado; null si no está asignado", example = "VIP")
    private String localityName;
    @Schema(description = "Estado actual del asiento (AVAILABLE/RESERVED/SOLD); null si no está asignado")
    private SeatStatus status;
}
