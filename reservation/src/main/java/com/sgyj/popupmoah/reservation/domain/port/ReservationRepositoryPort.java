package com.sgyj.popupmoah.reservation.domain.port;

import com.sgyj.popupmoah.reservation.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 리포지토리 포트
 * 도메인이 정의하는 인터페이스로, 인프라스트럭처가 구현
 */
public interface ReservationRepositoryPort {
    
    /**
     * 예약을 저장합니다.
     */
    Reservation save(Reservation reservation);
    
    /**
     * ID로 예약을 조회합니다.
     */
    Optional<Reservation> findById(Long id);
    
    /**
     * 회원 ID로 예약 목록을 조회합니다.
     */
    List<Reservation> findByMemberId(Long memberId);
    
    /**
     * 팝업스토어 ID로 예약 목록을 조회합니다.
     */
    List<Reservation> findByPopupStoreId(Long popupStoreId);
    
    /**
     * 특정 날짜의 예약 목록을 조회합니다.
     */
    List<Reservation> findByReservationDate(LocalDateTime date);
    
    /**
     * 특정 날짜 범위의 예약 목록을 조회합니다.
     */
    List<Reservation> findByReservationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 상태별 예약 목록을 조회합니다.
     */
    List<Reservation> findByStatus(String status);
    
    /**
     * 회원 ID와 상태로 예약 목록을 조회합니다.
     */
    List<Reservation> findByMemberIdAndStatus(Long memberId, String status);
    
    /**
     * 팝업스토어 ID와 상태로 예약 목록을 조회합니다.
     */
    List<Reservation> findByPopupStoreIdAndStatus(Long popupStoreId, String status);
    
    /**
     * 특정 시간에 중복 예약이 있는지 확인합니다.
     */
    boolean existsByPopupStoreIdAndReservationDateTime(Long popupStoreId, LocalDateTime reservationDateTime);
    
    /**
     * 예약을 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 예약이 존재하는지 확인합니다.
     */
    boolean existsById(Long id);
    
    /**
     * 만료된 예약 목록을 조회합니다.
     */
    List<Reservation> findExpiredReservations(LocalDateTime now);
}


