package com.sgyj.popupmoah.reservation.domain.aggregate;

import com.sgyj.popupmoah.reservation.domain.entity.Reservation;
import com.sgyj.popupmoah.reservation.domain.port.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 예약 애그리게이트 루트
 * 예약 도메인에 대한 접근을 제어하는 루트 엔티티
 */
@RequiredArgsConstructor
public class ReservationAggregate {
    
    private final ReservationRepositoryPort repository;
    
    /**
     * 예약을 생성합니다.
     */
    public Reservation create(Reservation reservation) {
        // 도메인 규칙 검증
        validateReservation(reservation);
        
        // 중복 예약 확인
        if (repository.existsByPopupStoreIdAndReservationDateTime(
                reservation.getPopupStoreId(), reservation.getReservationDateTime())) {
            throw new IllegalArgumentException("해당 시간에 이미 예약이 존재합니다.");
        }
        
        // 기본 상태 설정
        if (reservation.getStatus() == null) {
            reservation = Reservation.builder()
                    .id(reservation.getId())
                    .memberId(reservation.getMemberId())
                    .popupStoreId(reservation.getPopupStoreId())
                    .memberName(reservation.getMemberName())
                    .memberEmail(reservation.getMemberEmail())
                    .memberPhone(reservation.getMemberPhone())
                    .reservationDateTime(reservation.getReservationDateTime())
                    .numberOfPeople(reservation.getNumberOfPeople())
                    .status("PENDING")
                    .specialRequests(reservation.getSpecialRequests())
                    .notes(reservation.getNotes())
                    .build();
        }
        
        return repository.save(reservation);
    }
    
    /**
     * 예약을 조회합니다.
     */
    public Optional<Reservation> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * 회원의 예약 목록을 조회합니다.
     */
    public List<Reservation> findByMemberId(Long memberId) {
        return repository.findByMemberId(memberId);
    }
    
    /**
     * 팝업스토어의 예약 목록을 조회합니다.
     */
    public List<Reservation> findByPopupStoreId(Long popupStoreId) {
        return repository.findByPopupStoreId(popupStoreId);
    }
    
    /**
     * 예약을 업데이트합니다.
     */
    public Reservation update(Long id, Reservation reservation) {
        Optional<Reservation> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("예약을 찾을 수 없습니다: " + id);
        }
        
        // 도메인 규칙 검증
        validateReservation(reservation);
        
        Reservation updated = existing.get();
        // 불변성을 유지하면서 업데이트
        updated = Reservation.builder()
                .id(updated.getId())
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
        
        return repository.save(updated);
    }
    
    /**
     * 예약을 삭제합니다.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("예약을 찾을 수 없습니다: " + id);
        }
        repository.deleteById(id);
    }
    
    /**
     * 예약을 확인합니다.
     */
    public void confirm(Long id) {
        Optional<Reservation> reservation = repository.findById(id);
        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            res.confirm();
            repository.save(res);
        }
    }
    
    /**
     * 예약을 취소합니다.
     */
    public void cancel(Long id, String reason) {
        Optional<Reservation> reservation = repository.findById(id);
        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            res.cancel(reason);
            repository.save(res);
        }
    }
    
    /**
     * 예약을 완료합니다.
     */
    public void complete(Long id) {
        Optional<Reservation> reservation = repository.findById(id);
        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            res.complete();
            repository.save(res);
        }
    }
    
    /**
     * 예약 가능 여부를 확인합니다.
     */
    public boolean isReservationAvailable(Long popupStoreId, LocalDateTime reservationDateTime) {
        return !repository.existsByPopupStoreIdAndReservationDateTime(popupStoreId, reservationDateTime);
    }
    
    /**
     * 만료된 예약을 정리합니다.
     */
    public void cleanupExpiredReservations() {
        List<Reservation> expiredReservations = repository.findExpiredReservations(LocalDateTime.now());
        for (Reservation reservation : expiredReservations) {
            if (reservation.isPending()) {
                reservation.cancel("예약 시간 만료");
                repository.save(reservation);
            }
        }
    }
    
    /**
     * 예약 도메인 규칙을 검증합니다.
     */
    private void validateReservation(Reservation reservation) {
        if (reservation.getMemberId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }
        
        if (reservation.getPopupStoreId() == null) {
            throw new IllegalArgumentException("팝업스토어 ID는 필수입니다.");
        }
        
        if (reservation.getReservationDateTime() == null) {
            throw new IllegalArgumentException("예약 시간은 필수입니다.");
        }
        
        if (reservation.getReservationDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("예약 시간은 현재 시간보다 이후여야 합니다.");
        }
        
        if (reservation.getNumberOfPeople() == null || reservation.getNumberOfPeople() <= 0) {
            throw new IllegalArgumentException("예약 인원은 1명 이상이어야 합니다.");
        }
        
        if (reservation.getMemberName() == null || reservation.getMemberName().trim().isEmpty()) {
            throw new IllegalArgumentException("회원 이름은 필수입니다.");
        }
        
        if (reservation.getMemberEmail() == null || reservation.getMemberEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("회원 이메일은 필수입니다.");
        }
    }
}
