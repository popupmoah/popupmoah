package com.sgyj.popupmoah.reservation.infrastructure.web;

import com.sgyj.popupmoah.reservation.application.dto.*;
import com.sgyj.popupmoah.reservation.application.service.ReservationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 예약 관리 REST API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationApplicationService reservationService;

    /**
     * 예약 생성
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest request) {
        log.info("예약 생성 요청: {}", request);
        
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 예약 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        log.info("예약 상세 조회 요청: id={}", id);
        
        return reservationService.getReservationById(id)
                .map(reservation -> ResponseEntity.ok(reservation))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 예약 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest request) {
        log.info("예약 수정 요청: id={}, {}", id, request);
        
        ReservationResponse response = reservationService.updateReservation(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 예약 취소
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationCancelRequest request) {
        log.info("예약 취소 요청: id={}, reason={}", id, request.getReason());
        
        reservationService.cancelReservation(id, request.getReason());
        return ResponseEntity.ok().build();
    }

    /**
     * 예약 확인
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmReservation(@PathVariable Long id) {
        log.info("예약 확인 요청: id={}", id);
        
        reservationService.confirmReservation(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 예약 완료
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeReservation(@PathVariable Long id) {
        log.info("예약 완료 요청: id={}", id);
        
        reservationService.completeReservation(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원별 예약 목록 조회
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByMember(
            @PathVariable Long memberId,
            @RequestParam(required = false) String status) {
        log.info("회원별 예약 목록 조회 요청: memberId={}, status={}", memberId, status);
        
        List<ReservationResponse> reservations = reservationService.getReservations(memberId, null, status);
        return ResponseEntity.ok(reservations);
    }

    /**
     * 팝업스토어별 예약 목록 조회
     */
    @GetMapping("/popupstore/{popupStoreId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByPopupStore(
            @PathVariable Long popupStoreId,
            @RequestParam(required = false) String status) {
        log.info("팝업스토어별 예약 목록 조회 요청: popupStoreId={}, status={}", popupStoreId, status);
        
        List<ReservationResponse> reservations = reservationService.getReservations(null, popupStoreId, status);
        return ResponseEntity.ok(reservations);
    }

    /**
     * 예약 가능 여부 확인
     */
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long popupStoreId,
            @RequestParam String reservationDateTime) {
        log.info("예약 가능 여부 확인 요청: popupStoreId={}, dateTime={}", popupStoreId, reservationDateTime);
        
        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(reservationDateTime);
            boolean available = reservationService.isReservationAvailable(popupStoreId, dateTime);
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            log.error("예약 가능 여부 확인 중 오류 발생", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 만료된 예약 정리 (관리자용)
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Void> cleanupExpiredReservations() {
        log.info("만료된 예약 정리 요청");
        
        reservationService.cleanupExpiredReservations();
        return ResponseEntity.ok().build();
    }
}



