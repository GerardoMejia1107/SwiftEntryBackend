package com.gerardo.swiftentrybackend.domain.WaitingList.repositories;

import com.gerardo.swiftentrybackend.domain.WaitingList.WaitingListModel;
import com.gerardo.swiftentrybackend.domain.WaitingList.enums.WaitingListStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WaitingListRepository extends JpaRepository<WaitingListModel, Integer> {

    boolean existsByUser_IdAndLocality_IdAndStatusIn(
            Integer userId, Long localityId, Collection<WaitingListStatus> statuses);

    List<WaitingListModel> findByLocality_IdAndStatusOrderByCreatedAtAsc(
            Long localityId, WaitingListStatus status);

    List<WaitingListModel> findByUser_Id(Integer userId);

    List<WaitingListModel> findByLocality_Id(Long localityId);

    Optional<WaitingListModel> findByUser_IdAndLocality_IdAndStatus(
            Integer userId, Long localityId, WaitingListStatus status);

    @Query("SELECT w FROM WaitingListModel w WHERE w.status = :status AND w.notificationExpiresAt < :now")
    List<WaitingListModel> findExpiredNotifications(
            @Param("status") WaitingListStatus status,
            @Param("now") LocalDateTime now);
}
