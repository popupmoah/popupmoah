package com.sgyj.popupmoah.reservation.domain.aggregate;

import com.sgyj.popupmoah.reservation.domain.entity.Reservation;
import com.sgyj.popupmoah.reservation.domain.port.ReservationRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationAggregateTest {
    
    @Mock
    private ReservationRepositoryPort repository;
    
    private ReservationAggregate aggregate;
    
    private Reservation testReservation;
    
    @BeforeEach
    void setUp() {
        aggregate = new ReservationAggregate(repository);
        
        testReservation = Reservation.builder()
                .id(1L)
                .memberId(1L)
                .popupStoreId(1L)
                .memberName("홍길동")
                .memberEmail("hong@example.com")
                .memberPhone("010-1234-5678")
                .reservationDateTime(LocalDateTime.now().plusDays(1))
                .numberOfPeople(2)
                .status("PENDING")
                .specialRequests("창가 자리 부탁드립니다")
                .notes("첫 방문입니다")
                .build();
    }
    
    @Test
    @DisplayName("예약 생성 테스트")
    void testCreateReservation() {
        // Given
        when(repository.existsByPopupStoreIdAndReservationDateTime(anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);
        when(repository.save(any(Reservation.class))).thenReturn(testReservation);
        
        // When
        Reservation result = aggregate.create(testReservation);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMemberName()).isEqualTo("홍길동");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(repository).existsByPopupStoreIdAndReservationDateTime(1L, testReservation.getReservationDateTime());
        verify(repository).save(testReservation);
    }
    
    @Test
    @DisplayName("중복 예약 생성 시 예외 발생 테스트")
    void testCreateDuplicateReservation() {
        // Given
        when(repository.existsByPopupStoreIdAndReservationDateTime(anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> aggregate.create(testReservation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 시간에 이미 예약이 존재합니다");
        verify(repository).existsByPopupStoreIdAndReservationDateTime(1L, testReservation.getReservationDateTime());
        verify(repository, never()).save(any(Reservation.class));
    }
    
    @Test
    @DisplayName("예약 조회 테스트")
    void testFindById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testReservation));
        
        // When
        Optional<Reservation> result = aggregate.findById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getMemberName()).isEqualTo("홍길동");
        verify(repository).findById(1L);
    }
    
    @Test
    @DisplayName("존재하지 않는 예약 조회 테스트")
    void testFindByIdNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<Reservation> result = aggregate.findById(999L);
        
        // Then
        assertThat(result).isEmpty();
        verify(repository).findById(999L);
    }
    
    @Test
    @DisplayName("회원별 예약 조회 테스트")
    void testFindByMemberId() {
        // Given
        List<Reservation> memberReservations = Arrays.asList(testReservation);
        when(repository.findByMemberId(1L)).thenReturn(memberReservations);
        
        // When
        List<Reservation> result = aggregate.findByMemberId(1L);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMemberId()).isEqualTo(1L);
        verify(repository).findByMemberId(1L);
    }
    
    @Test
    @DisplayName("팝업스토어별 예약 조회 테스트")
    void testFindByPopupStoreId() {
        // Given
        List<Reservation> storeReservations = Arrays.asList(testReservation);
        when(repository.findByPopupStoreId(1L)).thenReturn(storeReservations);
        
        // When
        List<Reservation> result = aggregate.findByPopupStoreId(1L);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPopupStoreId()).isEqualTo(1L);
        verify(repository).findByPopupStoreId(1L);
    }
    
    @Test
    @DisplayName("예약 업데이트 테스트")
    void testUpdateReservation() {
        // Given
        Reservation updatedReservation = Reservation.builder()
                .memberName("김철수")
                .memberEmail("kim@example.com")
                .memberPhone("010-9876-5432")
                .reservationDateTime(LocalDateTime.now().plusDays(2))
                .numberOfPeople(4)
                .status("PENDING")
                .specialRequests("단체석 부탁드립니다")
                .notes("회사 모임입니다")
                .build();
        
        when(repository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(repository.save(any(Reservation.class))).thenReturn(updatedReservation);
        
        // When
        Reservation result = aggregate.update(1L, updatedReservation);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMemberName()).isEqualTo("김철수");
        verify(repository).findById(1L);
        verify(repository).save(any(Reservation.class));
    }
    
    @Test
    @DisplayName("존재하지 않는 예약 업데이트 시 예외 발생 테스트")
    void testUpdateReservationNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> aggregate.update(999L, testReservation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("예약을 찾을 수 없습니다");
        verify(repository).findById(999L);
        verify(repository, never()).save(any(Reservation.class));
    }
    
    @Test
    @DisplayName("예약 삭제 테스트")
    void testDeleteReservation() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);
        
        // When
        aggregate.delete(1L);
        
        // Then
        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }
    
    @Test
    @DisplayName("존재하지 않는 예약 삭제 시 예외 발생 테스트")
    void testDeleteReservationNotFound() {
        // Given
        when(repository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> aggregate.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("예약을 찾을 수 없습니다");
        verify(repository).existsById(999L);
        verify(repository, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("예약 확인 테스트")
    void testConfirmReservation() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(repository.save(any(Reservation.class))).thenReturn(testReservation);
        
        // When
        aggregate.confirm(1L);
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(Reservation.class));
    }
    
    @Test
    @DisplayName("예약 취소 테스트")
    void testCancelReservation() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(repository.save(any(Reservation.class))).thenReturn(testReservation);
        
        // When
        aggregate.cancel(1L, "개인 사정으로 취소");
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(Reservation.class));
    }
    
    @Test
    @DisplayName("예약 완료 테스트")
    void testCompleteReservation() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(repository.save(any(Reservation.class))).thenReturn(testReservation);
        
        // When
        aggregate.complete(1L);
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(Reservation.class));
    }
    
    @Test
    @DisplayName("예약 가능 여부 확인 테스트 - 가능한 경우")
    void testIsReservationAvailableTrue() {
        // Given
        when(repository.existsByPopupStoreIdAndReservationDateTime(1L, testReservation.getReservationDateTime()))
                .thenReturn(false);
        
        // When
        boolean result = aggregate.isReservationAvailable(1L, testReservation.getReservationDateTime());
        
        // Then
        assertThat(result).isTrue();
        verify(repository).existsByPopupStoreIdAndReservationDateTime(1L, testReservation.getReservationDateTime());
    }
    
    @Test
    @DisplayName("예약 가능 여부 확인 테스트 - 불가능한 경우")
    void testIsReservationAvailableFalse() {
        // Given
        when(repository.existsByPopupStoreIdAndReservationDateTime(1L, testReservation.getReservationDateTime()))
                .thenReturn(true);
        
        // When
        boolean result = aggregate.isReservationAvailable(1L, testReservation.getReservationDateTime());
        
        // Then
        assertThat(result).isFalse();
        verify(repository).existsByPopupStoreIdAndReservationDateTime(1L, testReservation.getReservationDateTime());
    }
    
    @Test
    @DisplayName("만료된 예약 정리 테스트")
    void testCleanupExpiredReservations() {
        // Given
        Reservation expiredReservation = Reservation.builder()
                .id(2L)
                .memberId(2L)
                .popupStoreId(2L)
                .memberName("이영희")
                .memberEmail("lee@example.com")
                .memberPhone("010-1111-2222")
                .reservationDateTime(LocalDateTime.now().minusHours(1))
                .numberOfPeople(1)
                .status("PENDING")
                .build();
        
        List<Reservation> expiredReservations = Arrays.asList(expiredReservation);
        when(repository.findExpiredReservations(any(LocalDateTime.class))).thenReturn(expiredReservations);
        when(repository.save(any(Reservation.class))).thenReturn(expiredReservation);
        
        // When
        aggregate.cleanupExpiredReservations();
        
        // Then
        verify(repository).findExpiredReservations(any(LocalDateTime.class));
        verify(repository).save(any(Reservation.class));
    }
}
