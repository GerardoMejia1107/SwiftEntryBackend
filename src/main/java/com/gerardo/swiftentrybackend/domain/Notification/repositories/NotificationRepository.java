package com.gerardo.swiftentrybackend.domain.Notification.repositories;

import com.gerardo.swiftentrybackend.domain.Notification.NotificationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationModel, Integer> {

    List<NotificationModel> findByUser_IdOrderByCreatedAtDesc(Integer userId);

    List<NotificationModel> findByUser_IdAndReadFalseOrderByCreatedAtDesc(Integer userId);

    long countByUser_IdAndReadFalse(Integer userId);
}
