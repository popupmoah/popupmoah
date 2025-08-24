package com.sgyj.popupmoah.reservation.infrastructure.persistence;

import com.sgyj.popupmoah.reservation.adapters.jpa.ReservationJpaRepository;
import com.sgyj.popupmoah.reservation.domain.entity.Reservation;
import com.sgyj.popupmoah.reservation.domain.port.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 리포지토리 어댑터
 * 도메인 포트와 JPA 구현체를 연결하는 어댑터
 */
@Component
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepositoryPort {

    private final ReservationJpaRepository jpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return jpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Reservation> findByMemberId(Long memberId) {
        return jpaRepository.findByMemberId(memberId);
    }

    @Override
    public List<Reservation> findByPopupStoreId(Long popupStoreId) {
        return jpaRepository.findByPopupStoreId(popupStoreId);
    }

    @Override
    public List<Reservation> findByReservationDate(LocalDateTime date) {
        return jpaRepository.findByReservationDate(date);
    }

    @Override
    public List<Reservation> findByReservationDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByReservationDateBetween(startDate, endDate);
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<Reservation> findByMemberIdAndStatus(Long memberId, String status) {
        return jpaRepository.findByMemberIdAndStatus(memberId, status);
    }

    @Override
    public List<Reservation> findByPopupStoreIdAndStatus(Long popupStoreId, String status) {
        return jpaRepository.findByPopupStoreIdAndStatus(popupStoreId, status);
    }

    @Override
    public boolean existsByPopupStoreIdAndReservationDateTime(Long popupStoreId, LocalDateTime reservationDateTime) {
        return jpaRepository.existsByPopupStoreIdAndReservationDateTime(popupStoreId, reservationDateTime);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Reservation> findExpiredReservations(LocalDateTime now) {
        return jpaRepository.findExpiredReservations(now);
    }
}
