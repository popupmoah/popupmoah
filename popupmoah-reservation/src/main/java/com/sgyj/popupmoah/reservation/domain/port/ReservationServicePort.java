package com.sgyj.popupmoah.reservation.domain.port;

import com.sgyj.popupmoah.reservation.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 서비스 포트
 * 도메인 비즈니스 로직을 위한 인터페이스
 */
public interface ReservationServicePort {
    
    /**
     * 예약을 생성합니다.
     */
    Reservation createReservation(Reservation reservation);
    
    /**
     * 예약을 조회합니다.
     */
    Optional<Reservation> getReservation(Long id);
    
    /**
     * 회원의 예약 목록을 조회합니다.
     */
    List<Reservation> getReservationsByMember(Long memberId);
    
    /**
     * 팝업스토어의 예약 목록을 조회합니다.
     */
    List<Reservation> getReservationsByPopupStore(Long popupStoreId);
    
    /**
     * 예약을 수정합니다.
     */
    Reservation updateReservation(Long id, Reservation reservation);
    
    /**
     * 예약을 취소합니다.
     */
    void cancelReservation(Long id, String reason);
    
    /**
     * 예약을 확인합니다.
     */
    void confirmReservation(Long id);
    
    /**
     * 예약을 완료합니다.
     */
    void completeReservation(Long id);
    
    /**
     * 만료된 예약을 정리합니다.
     */
    void cleanupExpiredReservations();
    
    /**
     * 예약 가능 여부를 확인합니다.
     */
    boolean isReservationAvailable(Long popupStoreId, LocalDateTime reservationDateTime);
    
    /**
     * 예약 알림을 발송합니다.
     */
    void sendReservationNotification(Reservation reservation, String notificationType);
}
