package com.gerardo.swiftentrybackend.domain.WaitingList.dto.response;

import com.gerardo.swiftentrybackend.domain.WaitingList.enums.WaitingListStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingListResponseDTO {

    private Integer id;
    private Integer userId;
    private String userEmail;
    private String userName;
    private Long localityId;
    private String localityName;
    private Integer eventId;
    private String eventName;
    private WaitingListStatus status;
    private LocalDateTime notifiedAt;
    private LocalDateTime notificationExpiresAt;
    private LocalDateTime fulfilledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
