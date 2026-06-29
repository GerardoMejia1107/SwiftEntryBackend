package com.gerardo.swiftentrybackend.domain.Notification.service;

import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Notification.NotificationModel;
import com.gerardo.swiftentrybackend.domain.Notification.dto.response.NotificationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Notification.enums.NotificationType;
import com.gerardo.swiftentrybackend.domain.Notification.repositories.NotificationRepository;
import com.gerardo.swiftentrybackend.domain.Notification.utils.NotificationMapper;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createNotification(UserModel user, NotificationType type, String title,
                                   String message, Long relatedEntityId) {
        NotificationModel notification = NotificationModel.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityId(relatedEntityId)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponseDTO> getMyNotifications(String userEmail) {
        UserModel user = getUser(userEmail);
        return notificationMapper.toResponseList(
                notificationRepository.findByUser_IdOrderByCreatedAtDesc(user.getId()));
    }

    @Override
    public List<NotificationResponseDTO> getMyUnread(String userEmail) {
        UserModel user = getUser(userEmail);
        return notificationMapper.toResponseList(
                notificationRepository.findByUser_IdAndReadFalseOrderByCreatedAtDesc(user.getId()));
    }

    @Override
    public long getMyUnreadCount(String userEmail) {
        UserModel user = getUser(userEmail);
        return notificationRepository.countByUser_IdAndReadFalse(user.getId());
    }

    @Override
    @Transactional
    public NotificationResponseDTO markAsRead(Integer id, String userEmail) {
        UserModel user = getUser(userEmail);
        NotificationModel notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("This notification does not belong to you.");
        }

        if (!notification.getRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userEmail) {
        UserModel user = getUser(userEmail);
        List<NotificationModel> unread =
                notificationRepository.findByUser_IdAndReadFalseOrderByCreatedAtDesc(user.getId());

        if (unread.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(now);
        });
        notificationRepository.saveAll(unread);
    }

    private UserModel getUser(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
    }
}
