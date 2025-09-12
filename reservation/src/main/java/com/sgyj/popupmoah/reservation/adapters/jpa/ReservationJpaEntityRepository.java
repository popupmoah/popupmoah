package com.sgyj.popupmoah.reservation.adapters.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예약 JPA 엔티티 리포지토리
 */
@Repository
public interface ReservationJpaEntityRepository extends JpaRepository<ReservationJpaEntity, Long> {

    /**
     * 회원 ID로 예약 목록 조회
     */
    List<ReservationJpaEntity> findByMemberIdOrderByReservationDateTimeDesc(Long memberId);

    /**
     * 팝업스토어 ID로 예약 목록 조회
     */
    List<ReservationJpaEntity> findByPopupStoreIdOrderByReservationDateTimeDesc(Long popupStoreId);

    /**
     * 특정 날짜의 예약 목록 조회
     */
    @Query("SELECT r FROM ReservationJpaEntity r WHERE DATE(r.reservationDateTime) = DATE(:date)")
    List<ReservationJpaEntity> findByReservationDate(@Param("date") LocalDateTime date);

    /**
     * 특정 날짜 범위의 예약 목록 조회
     */
    List<ReservationJpaEntity> findByReservationDateTimeBetweenOrderByReservationDateTimeDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 상태별 예약 목록 조회
     */
    List<ReservationJpaEntity> findByStatusOrderByReservationDateTimeDesc(String status);

    /**
     * 회원 ID와 상태로 예약 목록 조회
     */
    List<ReservationJpaEntity> findByMemberIdAndStatusOrderByReservationDateTimeDesc(
            Long memberId, String status);

    /**
     * 팝업스토어 ID와 상태로 예약 목록 조회
     */
    List<ReservationJpaEntity> findByPopupStoreIdAndStatusOrderByReservationDateTimeDesc(
            Long popupStoreId, String status);

    /**
     * 특정 시간에 중복 예약이 있는지 확인
     */
    boolean existsByPopupStoreIdAndReservationDateTime(Long popupStoreId, LocalDateTime reservationDateTime);

    /**
     * 만료된 예약 목록 조회
     */
    @Query("SELECT r FROM ReservationJpaEntity r WHERE r.reservationDateTime < :now AND r.status = 'PENDING'")
    List<ReservationJpaEntity> findExpiredReservations(@Param("now") LocalDateTime now);
}



