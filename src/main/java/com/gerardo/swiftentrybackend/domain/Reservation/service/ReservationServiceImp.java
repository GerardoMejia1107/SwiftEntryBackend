package com.gerardo.swiftentrybackend.domain.Reservation.service;

import com.gerardo.swiftentrybackend.common.exceptions.ForbiddenOperationException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceConflictException;
import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Locality.LocalityModel;
import com.gerardo.swiftentrybackend.domain.Locality.repositories.LocalityRepository;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationRequestDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.response.ReservationResponseDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.enums.ReservationStatus;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationRepository;
import com.gerardo.swiftentrybackend.domain.Reservation.repositories.ReservationSeatRepository;
import com.gerardo.swiftentrybackend.domain.Reservation.utils.ReservationMapper;
import com.gerardo.swiftentrybackend.domain.Seat.LocalitySeatModel;
import com.gerardo.swiftentrybackend.domain.Seat.enums.SeatStatus;
import com.gerardo.swiftentrybackend.domain.Seat.repositories.LocalitySeatRepository;
import com.gerardo.swiftentrybackend.domain.User.models.UserModel;
import com.gerardo.swiftentrybackend.domain.User.repositories.UserRepository;
import com.gerardo.swiftentrybackend.domain.WaitingList.service.WaitingListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Implementación del ciclo de vida de reservas: crear (con locks pesimistas), cancelar, expirar y consultar
@Service
@RequiredArgsConstructor
public class ReservationServiceImp implements ReservationService {

    private static final int MAX_SEATS_PER_RESERVATION = 5;
    private static final int RESERVATION_EXPIRY_MINUTES = 15;
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.13);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final LocalitySeatRepository localitySeatRepository;
    private final LocalityRepository localityRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final WaitingListService waitingListService;

    @Override
    @Transactional
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO, String userEmail) {
        if (requestDTO.getLocalitySeatIds().size() > MAX_SEATS_PER_RESERVATION) {
            throw new ResourceConflictException(
                    "Cannot reserve more than " + MAX_SEATS_PER_RESERVATION + " seats at a time.");
        }

        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        // Acquire pessimistic write locks on all requested seats atomically
        List<LocalitySeatModel> seats = localitySeatRepository.findAllByIdWithLock(
                requestDTO.getLocalitySeatIds());

        if (seats.size() != requestDTO.getLocalitySeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats were not found.");
        }

        // Verify all seats are available and belong to the same event (prevents cross-event reservations)
        Integer eventId = seats.get(0).getLocality().getEvent().getId();
        for (LocalitySeatModel ls : seats) {
            if (!ls.getStatus().equals(SeatStatus.AVAILABLE)) {
                throw new ResourceConflictException(
                        "Seat " + ls.getSeat().getRowLabel() + ls.getSeat().getSeatNumber() +
                        " is not available (status: " + ls.getStatus() + ").");
            }
            if (!ls.getLocality().getEvent().getId().equals(eventId)) {
                throw new ResourceConflictException(
                        "All seats must belong to the same event.");
            }
        }

        // Enforce global cap: count seats already held (PENDING or CONFIRMED) for this user+event
        long alreadyHeld = reservationSeatRepository.countByUserAndEventAndStatuses(
                user.getId(), eventId,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));
        if (alreadyHeld + requestDTO.getLocalitySeatIds().size() > MAX_SEATS_PER_RESERVATION) {
            throw new ResourceConflictException(
                    "Cannot hold more than " + MAX_SEATS_PER_RESERVATION +
                    " tickets for the same event (you already hold " + alreadyHeld + ").");
        }

        // Calculate amounts — effective price includes early bird discount if deadline is active
        BigDecimal subtotal = seats.stream()
                .map(ls -> effectivePrice(ls.getLocality()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountAmount = seats.stream()
                .map(ls -> ls.getLocality().getPrice().subtract(effectivePrice(ls.getLocality())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount   = subtotal.multiply(TAX_RATE);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        LocalDateTime now       = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(RESERVATION_EXPIRY_MINUTES);

        // Build reservation with its seat lines
        ReservationModel reservation = reservationMapper.toModel(
                user,
                ReservationStatus.PENDING,
                subtotal,
                taxAmount,
                discountAmount,
                totalAmount,
                now,
                expiresAt
        );

        for (LocalitySeatModel ls : seats) {
            reservation.getReservationSeats().add(
                    ReservationSeatModel.builder()
                            .reservation(reservation)
                            .localitySeat(ls)
                            .priceAtReservation(effectivePrice(ls.getLocality()))
                            .build()
            );
        }

        // Mark each seat as reserved
        Map<Long, LocalityModel> localityMap   = new HashMap<>();
        Map<Long, Integer>       restoreDeltas = new HashMap<>();

        for (LocalitySeatModel ls : seats) {
            ls.setStatus(SeatStatus.RESERVED);
            ls.setReservedByUser(user);
            ls.setReservationExpiresAt(expiresAt);

            LocalityModel loc = ls.getLocality();
            localityMap.put(loc.getId(), loc);
            restoreDeltas.merge(loc.getId(), 1, Integer::sum);
        }

        // Decrement availableSlots per locality
        localityMap.forEach((locId, loc) ->
                loc.setAvailableSlots(loc.getAvailableSlots() - restoreDeltas.get(locId))
        );

        localitySeatRepository.saveAll(seats);
        localityRepository.saveAll(new ArrayList<>(localityMap.values()));
        ReservationModel saved = reservationRepository.save(reservation);

        return reservationMapper.toResponse(saved);
    }

    // Lista todas las reservas (uso administrativo)
    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationMapper.toResponseList(reservationRepository.findAll());
    }

    @Override
    public ReservationResponseDTO getReservationById(Integer id) {
        ReservationModel reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return reservationMapper.toResponse(reservation);
    }

    // Lista las reservas del usuario autenticado
    @Override
    public List<ReservationResponseDTO> getMyReservations(String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        return reservationMapper.toResponseList(reservationRepository.findByUser_Id(user.getId()));
    }

    @Override
    @Transactional
    public ReservationResponseDTO cancelReservation(Integer id, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        ReservationModel reservation = reservationRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found or does not belong to you."));

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ForbiddenOperationException(
                    "Only PENDING reservations can be cancelled (current status: " +
                    reservation.getStatus() + ").");
        }

        releaseSeats(reservation);
        reservation.setStatus(ReservationStatus.CANCELLED);

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponseDTO removeSeatFromReservation(Integer reservationId, Integer reservationSeatId, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        ReservationModel reservation = reservationRepository.findByIdAndUser_Id(reservationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found or does not belong to you."));

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ForbiddenOperationException(
                    "Only PENDING reservations can be modified (current status: " +
                    reservation.getStatus() + ").");
        }

        ReservationSeatModel seatToRemove = reservation.getReservationSeats().stream()
                .filter(rs -> rs.getId().equals(reservationSeatId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat entry not found in this reservation."));

        // Release the locality seat back to available
        LocalitySeatModel ls = seatToRemove.getLocalitySeat();
        ls.setStatus(SeatStatus.AVAILABLE);
        ls.setReservedByUser(null);
        ls.setReservationExpiresAt(null);
        localitySeatRepository.save(ls);

        // Restore the available slot on its locality
        LocalityModel locality = ls.getLocality();
        locality.setAvailableSlots(locality.getAvailableSlots() + 1);
        localityRepository.save(locality);

        waitingListService.notifyNextInQueue(locality.getId(), 1);

        // Remove the seat line — orphanRemoval handles the DB delete
        reservation.getReservationSeats().remove(seatToRemove);

        // If no seats remain, cancel the whole reservation
        if (reservation.getReservationSeats().isEmpty()) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            return reservationMapper.toResponse(reservationRepository.save(reservation));
        }

        // Recalculate totals from the remaining seat prices (already discounted)
        BigDecimal subtotal = reservation.getReservationSeats().stream()
                .map(ReservationSeatModel::getPriceAtReservation)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount   = subtotal.multiply(TAX_RATE);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        reservation.setSubtotal(subtotal);
        reservation.setTaxAmount(taxAmount);
        reservation.setTotalAmount(totalAmount);

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    // Lista las reservas de todos los eventos organizados por el usuario dado
    @Override
    public List<ReservationResponseDTO> getReservationsByOrganizer(String organizerEmail) {
        UserModel organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + organizerEmail));
        return reservationMapper.toResponseList(
                reservationRepository.findByOrganizerOfEvent(organizer.getId()));
    }

    @Override
    @Transactional
    public Integer expirePendingReservations() {
        List<ReservationModel> expired = reservationRepository.findExpiredReservations(
                ReservationStatus.PENDING, LocalDateTime.now());

        if (expired.isEmpty()) return 0;

        for (ReservationModel reservation : expired) {
            releaseSeats(reservation);
            reservation.setStatus(ReservationStatus.EXPIRED);
        }

        reservationRepository.saveAll(expired);
        return expired.size();
    }

    private BigDecimal effectivePrice(LocalityModel locality) {
        BigDecimal pct = locality.getEarlyBirdDiscountPercentage();
        LocalDateTime deadline = locality.getEarlyBirdDeadline();
        if (pct != null && deadline != null && LocalDateTime.now().isBefore(deadline)) {
            return locality.getPrice()
                    .multiply(HUNDRED.subtract(pct))
                    .divide(HUNDRED, 2, RoundingMode.HALF_UP);
        }
        return locality.getPrice();
    }

    // ── Shared seat-release logic (cancel & expire) ────────────
    private void releaseSeats(ReservationModel reservation) {
        List<LocalitySeatModel> seats = reservation.getReservationSeats()
                .stream()
                .map(ReservationSeatModel::getLocalitySeat)
                .toList();

        // Accumulate slot deltas per locality before modifying anything
        Map<Long, LocalityModel> localityMap   = new HashMap<>();
        Map<Long, Integer>       restoreDeltas = new HashMap<>();

        for (LocalitySeatModel ls : seats) {
            ls.setStatus(SeatStatus.AVAILABLE);
            ls.setReservedByUser(null);
            ls.setReservationExpiresAt(null);

            LocalityModel loc = ls.getLocality();
            localityMap.put(loc.getId(), loc);
            restoreDeltas.merge(loc.getId(), 1, Integer::sum);
        }

        localitySeatRepository.saveAll(seats);

        localityMap.forEach((locId, loc) ->
                loc.setAvailableSlots(loc.getAvailableSlots() + restoreDeltas.get(locId))
        );
        localityRepository.saveAll(new ArrayList<>(localityMap.values()));

        restoreDeltas.forEach((locId, delta) ->
                waitingListService.notifyNextInQueue(locId, delta));
    }
}
