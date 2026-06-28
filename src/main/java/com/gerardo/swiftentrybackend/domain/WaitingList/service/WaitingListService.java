package com.gerardo.swiftentrybackend.domain.WaitingList.service;

import com.gerardo.swiftentrybackend.domain.WaitingList.dto.request.WaitingListRequestDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.response.WaitingListResponseDTO;

import java.util.List;

public interface WaitingListService {

    WaitingListResponseDTO joinWaitingList(WaitingListRequestDTO dto, String userEmail);

    WaitingListResponseDTO leaveWaitingList(Integer id, String userEmail);

    List<WaitingListResponseDTO> getMyEntries(String userEmail);

    List<WaitingListResponseDTO> getAllEntries();

    List<WaitingListResponseDTO> getEntriesByLocality(Long localityId);

    void notifyNextInQueue(Long localityId, int slotsReleased);

    void fulfillNotifiedEntry(Integer userId, Long localityId);

    int expireNotifiedEntries();
}
