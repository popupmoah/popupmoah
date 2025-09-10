package com.sgyj.popupmoah.popupstore.domain.aggregate;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
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
class PopupStoreAggregateTest {
    
    @Mock
    private PopupStoreRepositoryPort repository;
    
    private PopupStoreAggregate aggregate;
    
    private PopupStore testPopupStore;
    
    @BeforeEach
    void setUp() {
        aggregate = new PopupStoreAggregate(repository);
        
        testPopupStore = PopupStore.builder()
                .id(1L)
                .name("테스트 팝업스토어")
                .description("테스트 설명")
                .imageUrl("https://example.com/image.jpg")
                .sourceUrl("https://example.com")
                .category("패션")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 강남구")
                .active(true)
                .viewCount(0L)
                .likeCount(0L)
                .build();
    }
    
    @Test
    @DisplayName("팝업스토어 생성 테스트")
    void testCreatePopupStore() {
        // Given
        when(repository.save(any(PopupStore.class))).thenReturn(testPopupStore);
        
        // When
        PopupStore result = aggregate.create(testPopupStore);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 팝업스토어");
        verify(repository).save(testPopupStore);
    }
    
    @Test
    @DisplayName("팝업스토어 조회 테스트")
    void testFindById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPopupStore));
        
        // When
        Optional<PopupStore> result = aggregate.findById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("테스트 팝업스토어");
        verify(repository).findById(1L);
    }
    
    @Test
    @DisplayName("존재하지 않는 팝업스토어 조회 테스트")
    void testFindByIdNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<PopupStore> result = aggregate.findById(999L);
        
        // Then
        assertThat(result).isEmpty();
        verify(repository).findById(999L);
    }
    
    @Test
    @DisplayName("모든 팝업스토어 조회 테스트")
    void testFindAll() {
        // Given
        List<PopupStore> popupStores = Arrays.asList(testPopupStore);
        when(repository.findAll()).thenReturn(popupStores);
        
        // When
        List<PopupStore> result = aggregate.findAll();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("테스트 팝업스토어");
        verify(repository).findAll();
    }
    
    @Test
    @DisplayName("활성화된 팝업스토어 조회 테스트")
    void testFindActive() {
        // Given
        List<PopupStore> activeStores = Arrays.asList(testPopupStore);
        when(repository.findByActiveTrue()).thenReturn(activeStores);
        
        // When
        List<PopupStore> result = aggregate.findActive();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
        verify(repository).findByActiveTrue();
    }
    
    @Test
    @DisplayName("팝업스토어 업데이트 테스트")
    void testUpdatePopupStore() {
        // Given
        PopupStore updatedStore = PopupStore.builder()
                .name("업데이트된 팝업스토어")
                .description("업데이트된 설명")
                .imageUrl("https://example.com/updated-image.jpg")
                .sourceUrl("https://example.com/updated")
                .category("뷰티")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .location("서울시 서초구")
                .active(true)
                .viewCount(0L)
                .likeCount(0L)
                .build();
        
        when(repository.findById(1L)).thenReturn(Optional.of(testPopupStore));
        when(repository.save(any(PopupStore.class))).thenReturn(updatedStore);
        
        // When
        PopupStore result = aggregate.update(1L, updatedStore);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("업데이트된 팝업스토어");
        verify(repository).findById(1L);
        verify(repository).save(any(PopupStore.class));
    }
    
    @Test
    @DisplayName("존재하지 않는 팝업스토어 업데이트 시 예외 발생 테스트")
    void testUpdatePopupStoreNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> aggregate.update(999L, testPopupStore))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("팝업스토어를 찾을 수 없습니다");
        verify(repository).findById(999L);
        verify(repository, never()).save(any(PopupStore.class));
    }
    
    @Test
    @DisplayName("팝업스토어 삭제 테스트")
    void testDeletePopupStore() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);
        
        // When
        aggregate.delete(1L);
        
        // Then
        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }
    
    @Test
    @DisplayName("존재하지 않는 팝업스토어 삭제 시 예외 발생 테스트")
    void testDeletePopupStoreNotFound() {
        // Given
        when(repository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> aggregate.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("팝업스토어를 찾을 수 없습니다");
        verify(repository).existsById(999L);
        verify(repository, never()).deleteById(anyLong());
    }
    
    @Test
    @DisplayName("조회수 증가 테스트")
    void testIncrementViewCount() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPopupStore));
        when(repository.save(any(PopupStore.class))).thenReturn(testPopupStore);
        
        // When
        aggregate.incrementViewCount(1L);
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(PopupStore.class));
    }
    
    @Test
    @DisplayName("좋아요 수 증가 테스트")
    void testIncrementLikeCount() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPopupStore));
        when(repository.save(any(PopupStore.class))).thenReturn(testPopupStore);
        
        // When
        aggregate.incrementLikeCount(1L);
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(PopupStore.class));
    }
    
    @Test
    @DisplayName("좋아요 수 감소 테스트")
    void testDecrementLikeCount() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPopupStore));
        when(repository.save(any(PopupStore.class))).thenReturn(testPopupStore);
        
        // When
        aggregate.decrementLikeCount(1L);
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(PopupStore.class));
    }
    
    @Test
    @DisplayName("팝업스토어 활성화 테스트")
    void testActivate() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPopupStore));
        when(repository.save(any(PopupStore.class))).thenReturn(testPopupStore);
        
        // When
        aggregate.activate(1L);
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(PopupStore.class));
    }
    
    @Test
    @DisplayName("팝업스토어 비활성화 테스트")
    void testDeactivate() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPopupStore));
        when(repository.save(any(PopupStore.class))).thenReturn(testPopupStore);
        
        // When
        aggregate.deactivate(1L);
        
        // Then
        verify(repository).findById(1L);
        verify(repository).save(any(PopupStore.class));
    }
    
    @Test
    @DisplayName("이름으로 팝업스토어 검색 테스트")
    void testSearchByName() {
        // Given
        List<PopupStore> searchResults = Arrays.asList(testPopupStore);
        when(repository.findByNameContaining("테스트")).thenReturn(searchResults);
        
        // When
        List<PopupStore> result = aggregate.searchByName("테스트");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("테스트");
        verify(repository).findByNameContaining("테스트");
    }
    
    @Test
    @DisplayName("카테고리로 팝업스토어 조회 테스트")
    void testFindByCategory() {
        // Given
        List<PopupStore> categoryResults = Arrays.asList(testPopupStore);
        when(repository.findByCategory("패션")).thenReturn(categoryResults);
        
        // When
        List<PopupStore> result = aggregate.findByCategory("패션");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("패션");
        verify(repository).findByCategory("패션");
    }
    
    @Test
    @DisplayName("위치로 팝업스토어 조회 테스트")
    void testFindByLocation() {
        // Given
        List<PopupStore> locationResults = Arrays.asList(testPopupStore);
        when(repository.findByLocation("서울시 강남구")).thenReturn(locationResults);
        
        // When
        List<PopupStore> result = aggregate.findByLocation("서울시 강남구");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocation()).isEqualTo("서울시 강남구");
        verify(repository).findByLocation("서울시 강남구");
    }
}
