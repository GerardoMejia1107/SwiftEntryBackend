package com.gerardo.swiftentrybackend.domain.WaitingList.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingListRequestDTO {

    @NotNull(message = "Locality ID is required")
    private Long localityId;
}
