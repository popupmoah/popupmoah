package com.sgyj.popupmoah.reservation.application.service;

import com.sgyj.popupmoah.reservation.application.dto.ReservationCreateRequest;
import com.sgyj.popupmoah.reservation.application.dto.ReservationResponse;
import com.sgyj.popupmoah.reservation.application.dto.ReservationUpdateRequest;
import com.sgyj.popupmoah.reservation.domain.aggregate.ReservationAggregate;
import com.sgyj.popupmoah.reservation.domain.entity.Reservation;
import com.sgyj.popupmoah.reservation.domain.port.ReservationRepositoryPort;
import com.sgyj.popupmoah.reservation.domain.port.ReservationServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 예약 애플리케이션 서비스
 * 유스케이스를 구현하는 애플리케이션 레이어
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationApplicationService implements ReservationServicePort {
    
    private final ReservationRepositoryPort repository;
    private final ReservationAggregate aggregate;
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "reservations", key = "'member:' + #reservation.memberId"),
        @CacheEvict(value = "reservations", key = "'popupstore:' + #reservation.popupStoreId")
    })
    public Reservation createReservation(Reservation reservation) {
        log.info("예약 생성 요청: memberId={}, popupStoreId={}, dateTime={}", 
                reservation.getMemberId(), reservation.getPopupStoreId(), reservation.getReservationDateTime());
        return aggregate.create(reservation);
    }
    
    @Override
    @Cacheable(value = "reservations", key = "#id")
    public Optional<Reservation> getReservation(Long id) {
        log.debug("예약 조회 (캐시 미스): id={}", id);
        return aggregate.findById(id);
    }
    
    @Override
    @Cacheable(value = "reservations", key = "'member:' + #memberId")
    public List<Reservation> getReservationsByMember(Long memberId) {
        log.debug("회원별 예약 조회 (캐시 미스): memberId={}", memberId);
        return aggregate.findByMemberId(memberId);
    }
    
    @Override
    @Cacheable(value = "reservations", key = "'popupstore:' + #popupStoreId")
    public List<Reservation> getReservationsByPopupStore(Long popupStoreId) {
        log.debug("팝업스토어별 예약 조회 (캐시 미스): popupStoreId={}", popupStoreId);
        return aggregate.findByPopupStoreId(popupStoreId);
    }
    
    @Override
    public Reservation updateReservation(Long id, Reservation reservation) {
        log.info("예약 수정 요청: id={}", id);
        return aggregate.update(id, reservation);
    }
    
    @Override
    public void cancelReservation(Long id, String reason) {
        log.info("예약 취소 요청: id={}, reason={}", id, reason);
        aggregate.cancel(id, reason);
    }
    
    @Override
    public void confirmReservation(Long id) {
        log.info("예약 확인 요청: id={}", id);
        aggregate.confirm(id);
    }
    
    @Override
    public void completeReservation(Long id) {
        log.info("예약 완료 요청: id={}", id);
        aggregate.complete(id);
    }
    
    @Override
    public void cleanupExpiredReservations() {
        log.info("만료된 예약 정리 요청");
        aggregate.cleanupExpiredReservations();
    }
    
    @Override
    public boolean isReservationAvailable(Long popupStoreId, LocalDateTime reservationDateTime) {
        log.info("예약 가능 여부 확인: popupStoreId={}, dateTime={}", popupStoreId, reservationDateTime);
        return aggregate.isReservationAvailable(popupStoreId, reservationDateTime);
    }
    
    @Override
    public void sendReservationNotification(Reservation reservation, String notificationType) {
        log.info("예약 알림 발송: reservationId={}, type={}", reservation.getId(), notificationType);
        // TODO: 실제 알림 발송 로직 구현 (이메일, SMS, 푸시 알림 등)
    }

    /**
     * 예약을 생성합니다.
     */
    @Transactional(readOnly = true)
    public ReservationResponse createReservation(ReservationCreateRequest request) {
        log.info("예약 생성 요청: {}", request);

        Reservation reservation = Reservation.builder()
                .memberId(request.getMemberId())
                .popupStoreId(request.getPopupStoreId())
                .memberName(request.getMemberName())
                .memberEmail(request.getMemberEmail())
                .memberPhone(request.getMemberPhone())
                .reservationDateTime(request.getReservationDateTime())
                .numberOfPeople(request.getNumberOfPeople())
                .specialRequests(request.getSpecialRequests())
                .notes(request.getNotes())
                .build();

        Reservation created = aggregate.create(reservation);
        return convertToResponse(created);
    }

    /**
     * 예약을 수정합니다.
     */
    @Transactional(readOnly = true)
    public ReservationResponse updateReservation(Long id, ReservationUpdateRequest request) {
        log.info("예약 수정 요청: id={}, {}", id, request);

        Reservation reservation = Reservation.builder()
                .memberId(request.getMemberId())
                .popupStoreId(request.getPopupStoreId())
                .memberName(request.getMemberName())
                .memberEmail(request.getMemberEmail())
                .memberPhone(request.getMemberPhone())
                .reservationDateTime(request.getReservationDateTime())
                .numberOfPeople(request.getNumberOfPeople())
                .specialRequests(request.getSpecialRequests())
                .notes(request.getNotes())
                .build();

        Reservation updated = aggregate.update(id, reservation);
        return convertToResponse(updated);
    }

    /**
     * 예약 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(Long memberId, Long popupStoreId, String status) {
        log.info("예약 목록 조회 요청: memberId={}, popupStoreId={}, status={}", memberId, popupStoreId, status);

        List<Reservation> reservations;
        if (memberId != null) {
            if (status != null) {
                reservations = repository.findByMemberIdAndStatus(memberId, status);
            } else {
                reservations = aggregate.findByMemberId(memberId);
            }
        } else if (popupStoreId != null) {
            if (status != null) {
                reservations = repository.findByPopupStoreIdAndStatus(popupStoreId, status);
            } else {
                reservations = aggregate.findByPopupStoreId(popupStoreId);
            }
        } else {
            throw new IllegalArgumentException("회원 ID 또는 팝업스토어 ID 중 하나는 필수입니다.");
        }

        return reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 예약 상세 정보를 조회합니다.
     */
    @Transactional(readOnly = true)
    public Optional<ReservationResponse> getReservationById(Long id) {
        log.info("예약 상세 조회 요청: id={}", id);
        return aggregate.findById(id)
                .map(this::convertToResponse);
    }

    /**
     * Reservation 엔티티를 ReservationResponse DTO로 변환
     */
    private ReservationResponse convertToResponse(Reservation reservation) {
        return ReservationResponse.builder()
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
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
