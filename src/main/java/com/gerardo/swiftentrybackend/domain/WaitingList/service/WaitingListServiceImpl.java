package com.gerardo.swiftentrybackend.domain.WaitingList.service;

import com.gerardo.swiftentrybackend.common.exceptions.BadRequestException;
import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Notification.enums.NotificationType;
import com.gerardo.swiftentrybackend.domain.Notification.service.NotificationService;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import com.gerardo.swiftentrybackend.domain.WaitingList.WaitingListModel;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.request.WaitingListRequestDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.dto.response.WaitingListResponseDTO;
import com.gerardo.swiftentrybackend.domain.WaitingList.enums.WaitingListStatus;
import com.gerardo.swiftentrybackend.domain.WaitingList.repositories.WaitingListRepository;
import com.gerardo.swiftentrybackend.domain.WaitingList.utils.WaitingListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
// Implementa el ciclo de vida de la lista de espera: alta, baja, notificación y expiración de turnos
public class WaitingListServiceImpl implements WaitingListService {

    // Minutos que un usuario notificado tiene para reservar antes de perder su turno
    private static final int NOTIFICATION_WINDOW_MINUTES = 30;

    private final WaitingListRepository waitingListRepository;
    private final LocalityRepository localityRepository;
    private final UserRepository userRepository;
    private final WaitingListMapper waitingListMapper;
    private final NotificationService notificationService;

    // Valida que la localidad no tenga cupo y que el usuario no tenga ya una entrada activa antes de anotarlo
    @Override
    @Transactional
    public WaitingListResponseDTO joinWaitingList(WaitingListRequestDTO dto, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        LocalityModel locality = localityRepository.findById(dto.getLocalityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Locality not found with id: " + dto.getLocalityId()));

        if (locality.getAvailableSlots() > 0) {
            throw new BadRequestException(
                    "Locality has available seats. Please make a reservation directly.");
        }

        if (waitingListRepository.existsByUser_IdAndLocality_IdAndStatusIn(
                user.getId(), locality.getId(), List.of(WaitingListStatus.WAITING, WaitingListStatus.NOTIFIED))) {
            throw new ResourceConflictException(
                    "You already have an active waiting list entry for this locality.");
        }

        WaitingListModel entry = WaitingListModel.builder()
                .user(user)
                .locality(locality)
                .status(WaitingListStatus.WAITING)
                .build();

        return waitingListMapper.toResponse(waitingListRepository.save(entry));
    }

    // Cancela la entrada del usuario; solo permitido si está en estado WAITING o NOTIFIED
    @Override
    @Transactional
    public WaitingListResponseDTO leaveWaitingList(Integer id, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        WaitingListModel entry = waitingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Waiting list entry not found with id: " + id));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException(
                    "This waiting list entry does not belong to you.");
        }

        if (entry.getStatus() != WaitingListStatus.WAITING
                && entry.getStatus() != WaitingListStatus.NOTIFIED) {
            throw new ForbiddenOperationException(
                    "Cannot cancel a waiting list entry with status: " + entry.getStatus());
        }

        entry.setStatus(WaitingListStatus.CANCELLED);
        return waitingListMapper.toResponse(waitingListRepository.save(entry));
    }

    @Override
    public List<WaitingListResponseDTO> getMyEntries(String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        return waitingListMapper.toResponseList(waitingListRepository.findByUser_Id(user.getId()));
    }

    @Override
    public List<WaitingListResponseDTO> getAllEntries() {
        return waitingListMapper.toResponseList(waitingListRepository.findAll());
    }

    @Override
    public List<WaitingListResponseDTO> getEntriesByLocality(Long localityId) {
        if (!localityRepository.existsById(localityId)) {
            throw new ResourceNotFoundException("Locality not found with id: " + localityId);
        }
        return waitingListMapper.toResponseList(waitingListRepository.findByLocality_Id(localityId));
    }

    // Pasa a NOTIFIED a los primeros usuarios en cola (por antigüedad), uno por cada cupo liberado,
    // y les fija la ventana de expiración de la notificación
    @Override
    @Transactional
    public void notifyNextInQueue(Long localityId, int slotsReleased) {
        if (slotsReleased <= 0) return;

        List<WaitingListModel> waiting = waitingListRepository
                .findByLocality_IdAndStatusOrderByCreatedAtAsc(localityId, WaitingListStatus.WAITING);

        if (waiting.isEmpty()) return;

        int toNotify = Math.min(slotsReleased, waiting.size());
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < toNotify; i++) {
            WaitingListModel entry = waiting.get(i);
            entry.setStatus(WaitingListStatus.NOTIFIED);
            entry.setNotifiedAt(now);
            entry.setNotificationExpiresAt(now.plusMinutes(NOTIFICATION_WINDOW_MINUTES));
            notifyUserSeatAvailable(entry);
        }

        waitingListRepository.saveAll(waiting.subList(0, toNotify));
    }

    // Si el usuario tiene una entrada NOTIFIED para esa localidad, la marca como FULFILLED (silencioso si no existe)
    @Override
    @Transactional
    public void fulfillNotifiedEntry(Integer userId, Long localityId) {
        waitingListRepository
                .findByUser_IdAndLocality_IdAndStatus(userId, localityId, WaitingListStatus.NOTIFIED)
                .ifPresent(entry -> {
                    entry.setStatus(WaitingListStatus.FULFILLED);
                    entry.setFulfilledAt(LocalDateTime.now());
                    waitingListRepository.save(entry);
                });
    }

    // Expira las notificaciones NOTIFIED vencidas y reintenta notificar al siguiente en cola por localidad afectada
    @Override
    @Transactional
    public int expireNotifiedEntries() {
        List<WaitingListModel> expired = waitingListRepository
                .findExpiredNotifications(WaitingListStatus.NOTIFIED, LocalDateTime.now());

        if (expired.isEmpty()) return 0;

        // Accumulate how many notifications expired per locality
        Map<Long, Integer> expiredByLocality = new HashMap<>();
        for (WaitingListModel entry : expired) {
            expiredByLocality.merge(entry.getLocality().getId(), 1, Integer::sum);
            entry.setStatus(WaitingListStatus.EXPIRED);
        }

        // Flush EXPIRED status before querying for the next WAITING entries below
        waitingListRepository.saveAll(expired);

        LocalDateTime now = LocalDateTime.now();

        // Re-notify next in queue for each affected locality (logic inlined to avoid self-invocation)
        expiredByLocality.forEach((locId, expiredCount) ->
                localityRepository.findById(locId).ifPresent(locality -> {
                    if (locality.getAvailableSlots() <= 0) return;

                    List<WaitingListModel> next = waitingListRepository
                            .findByLocality_IdAndStatusOrderByCreatedAtAsc(locId, WaitingListStatus.WAITING);

                    if (next.isEmpty()) return;

                    int toNotify = Math.min(expiredCount,
                            Math.min(locality.getAvailableSlots(), next.size()));

                    for (int i = 0; i < toNotify; i++) {
                        WaitingListModel entry = next.get(i);
                        entry.setStatus(WaitingListStatus.NOTIFIED);
                        entry.setNotifiedAt(now);
                        entry.setNotificationExpiresAt(now.plusMinutes(NOTIFICATION_WINDOW_MINUTES));
                        notifyUserSeatAvailable(entry);
                    }

                    waitingListRepository.saveAll(next.subList(0, toNotify));
                })
        );

        return expired.size();
    }

    // Crea la notificación de "asiento disponible" para el usuario de la entrada dada
    private void notifyUserSeatAvailable(WaitingListModel entry) {
        notificationService.createNotification(
                entry.getUser(),
                NotificationType.WAITING_LIST_SEAT_AVAILABLE,
                "A seat is now available",
                "A seat just became available for " + entry.getLocality().getName()
                        + ". You have " + NOTIFICATION_WINDOW_MINUTES
                        + " minutes to complete your reservation before your spot is released.",
                entry.getLocality().getId());
    }
}
