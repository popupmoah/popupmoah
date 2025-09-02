package com.sgyj.popupmoah.reservation.adapters.jpa;

import com.sgyj.popupmoah.reservation.domain.entity.Reservation;
import com.sgyj.popupmoah.reservation.domain.port.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 예약 JPA 리포지토리 어댑터
 * 도메인과 인프라스트럭처를 연결하는 어댑터
 */
@Repository
@RequiredArgsConstructor
public class ReservationJpaRepository implements ReservationRepositoryPort {

    private final ReservationJpaEntityRepository jpaEntityRepository;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity jpaEntity = toJpaEntity(reservation);
        ReservationJpaEntity saved = jpaEntityRepository.save(jpaEntity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpaEntityRepository.findById(id)
                .map(this::toDomainEntity);
    }

    @Override
    public List<Reservation> findByMemberId(Long memberId) {
        return jpaEntityRepository.findByMemberIdOrderByReservationDateTimeDesc(memberId)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByPopupStoreId(Long popupStoreId) {
        return jpaEntityRepository.findByPopupStoreIdOrderByReservationDateTimeDesc(popupStoreId)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByReservationDate(LocalDateTime date) {
        return jpaEntityRepository.findByReservationDate(date)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByReservationDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaEntityRepository.findByReservationDateTimeBetweenOrderByReservationDateTimeDesc(startDate, endDate)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        return jpaEntityRepository.findByStatusOrderByReservationDateTimeDesc(status)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByMemberIdAndStatus(Long memberId, String status) {
        return jpaEntityRepository.findByMemberIdAndStatusOrderByReservationDateTimeDesc(memberId, status)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByPopupStoreIdAndStatus(Long popupStoreId, String status) {
        return jpaEntityRepository.findByPopupStoreIdAndStatusOrderByReservationDateTimeDesc(popupStoreId, status)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPopupStoreIdAndReservationDateTime(Long popupStoreId, LocalDateTime reservationDateTime) {
        return jpaEntityRepository.existsByPopupStoreIdAndReservationDateTime(popupStoreId, reservationDateTime);
    }

    @Override
    public void deleteById(Long id) {
        jpaEntityRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaEntityRepository.existsById(id);
    }

    @Override
    public List<Reservation> findExpiredReservations(LocalDateTime now) {
        return jpaEntityRepository.findExpiredReservations(now)
                .stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    /**
     * 도메인 엔티티를 JPA 엔티티로 변환
     */
    private ReservationJpaEntity toJpaEntity(Reservation reservation) {
        return ReservationJpaEntity.builder()
                .id(reservation.getId())
                .memberId(reservation.getMemberId())
                .popupStoreId(reservation.getPopupStoreId())
                .memberName(reservation.getMemberName())
                .memberEmail(reservation.getMemberEmail())
                .memberPhone(reservation.getMemberPhone())
                .reservationDateTime(reservation.getReservationDateTime())
                .numberOfPeople(reservation.getNumberOfPeople())
                .status(reservation.getStatus())
                .specialRequests(reservation.getSpecialRequests())
                .notes(reservation.getNotes())
                .confirmedAt(reservation.getConfirmedAt())
                .cancelledAt(reservation.getCancelledAt())
                .cancellationReason(reservation.getCancellationReason())
                .build();
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환
     */
    private Reservation toDomainEntity(ReservationJpaEntity jpaEntity) {
        return Reservation.builder()
                .id(jpaEntity.getId())
                .memberId(jpaEntity.getMemberId())
                .popupStoreId(jpaEntity.getPopupStoreId())
                .memberName(jpaEntity.getMemberName())
                .memberEmail(jpaEntity.getMemberEmail())
                .memberPhone(jpaEntity.getMemberPhone())
                .reservationDateTime(jpaEntity.getReservationDateTime())
                .numberOfPeople(jpaEntity.getNumberOfPeople())
                .status(jpaEntity.getStatus())
                .specialRequests(jpaEntity.getSpecialRequests())
                .notes(jpaEntity.getNotes())
                .confirmedAt(jpaEntity.getConfirmedAt())
                .cancelledAt(jpaEntity.getCancelledAt())
                .cancellationReason(jpaEntity.getCancellationReason())
                .build();
    }
}
