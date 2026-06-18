package com.gerardo.swiftentrybackend.domain.Reservation.service;

import com.gerardo.swiftentrybackend.common.exceptions.ResourceNotFoundException;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationModel;
import com.gerardo.swiftentrybackend.domain.Reservation.ReservationSeatModel;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationRequestDTO;
import com.gerardo.swiftentrybackend.domain.Reservation.dto.request.ReservationUpdateDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImp implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final LocalitySeatRepository localitySeatRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO) {
        UserModel userMakingReservation = userRepository.findById(requestDTO.getUserId())
                .orElseThrow();

        List<LocalitySeatModel> seatsReserved = localitySeatRepository.findAllByIdWithLock(requestDTO.getSeatIds());

        if (seatsReserved.size() != requestDTO.getSeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats not found");
        }

        seatsReserved.forEach(localitySeat -> {
            if (!localitySeat.getStatus().equals(SeatStatus.AVAILABLE)) {
                throw new ResourceNotFoundException("One or more seats are not available");
            }
        });

        BigDecimal subtotal = seatsReserved.stream()
                .map(localitySeat -> localitySeat.getLocality().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.13));
        BigDecimal discountAmount = BigDecimal.valueOf(0.00);
        BigDecimal totalAmount = subtotal.add(taxAmount).subtract(discountAmount);

        ReservationModel reservationMade = reservationMapper.toModel(
                userMakingReservation,
                ReservationStatus.PENDING,
                subtotal,
                taxAmount,
                discountAmount,
                totalAmount,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );

        seatsReserved.forEach(localitySeat -> {
            reservationMade.getReservationSeats().add(
                    ReservationSeatModel.builder()
                            .reservation(reservationMade)
                            .localitySeat(localitySeat)
                            .priceAtReservation(localitySeat.getLocality().getPrice())
                            .build()
            );
            localitySeat.setStatus(SeatStatus.RESERVED);
        });

        localitySeatRepository.saveAll(seatsReserved);
        ReservationModel saved = reservationRepository.save(reservationMade);

        return reservationMapper.toResponse(saved);
    }

    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        return List.of();
    }

    @Override
    public ReservationResponseDTO getReservationById(Integer id) {
        return null;
    }

    @Override
    public List<ReservationResponseDTO> getReservationsByUserId(Integer userId) {
        return List.of();
    }

    @Override
    public List<ReservationResponseDTO> getReservationsByStatus(ReservationStatus status) {
        return List.of();
    }

    @Override
    public ReservationResponseDTO updateReservation(Integer id, ReservationUpdateDTO updateDTO) {
        return null;
    }

    @Override
    public ReservationResponseDTO cancelReservation(Integer id) {
        return null;
    }

    @Override
    public ReservationResponseDTO expireReservation(Integer id) {
        return null;
    }

    @Override
    public Integer expirePendingReservations() {
        return 0;
    }
}
