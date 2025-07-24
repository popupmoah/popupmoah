package com.sgyj.popupmoah.module.popupstore.service;

import com.sgyj.popupmoah.module.popupstore.controller.request.CreatePopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import com.sgyj.popupmoah.infra.jpa.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PopupStoreServiceTest {

    @Mock
    private PopupStoreRepository popupStoreRepository;

    private PopupStoreService popupStoreService;

    @BeforeEach
    void setUp() {
        popupStoreService = new PopupStoreService(popupStoreRepository);
    }

    @Test
    @DisplayName("팝업스토어 등록 성공")
    void createPopupStore_success() {
        // given
        CreatePopupStoreRequest request = mock(CreatePopupStoreRequest.class);
        PopupStore popupStore = mock(PopupStore.class);
        when(request.mapToEntity()).thenReturn(popupStore);
        when(popupStoreRepository.save(popupStore)).thenReturn(popupStore);
        when(popupStore.getId()).thenReturn(1L);
        when(popupStore.getName()).thenReturn("테스트");
        when(popupStore.getDescription()).thenReturn("설명");
        when(popupStore.getStartDate()).thenReturn(LocalDateTime.now());
        when(popupStore.getEndDate()).thenReturn(LocalDateTime.now());
        when(popupStore.getReservationDate()).thenReturn(LocalDateTime.now());
        Location location = mock(Location.class);
        when(popupStore.getLocation()).thenReturn(location);

        // when
        PopupStoreDto result = popupStoreService.createPopupStore(request);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트");
        verify(popupStoreRepository).save(popupStore);
    }

    @Test
    @DisplayName("팝업스토어 단건 조회 성공")
    void getPopupStore_success() {
        // given
        PopupStore popupStore = mock(PopupStore.class);
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.of(popupStore));
        when(popupStore.getId()).thenReturn(1L);
        when(popupStore.getName()).thenReturn("테스트");
        when(popupStore.getDescription()).thenReturn("설명");
        when(popupStore.getStartDate()).thenReturn(LocalDateTime.now());
        when(popupStore.getEndDate()).thenReturn(LocalDateTime.now());
        when(popupStore.getReservationDate()).thenReturn(LocalDateTime.now());
        Location location = mock(Location.class);
        when(popupStore.getLocation()).thenReturn(location);

        // when
        PopupStoreDto result = popupStoreService.getPopupStore(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트");
    }

    @Test
    @DisplayName("팝업스토어 단건 조회 실패 - 존재하지 않음")
    void getPopupStore_notFound() {
        // given
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> popupStoreService.getPopupStore(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않습니다");
    }

    @Test
    @DisplayName("팝업스토어 전체 조회")
    void getAllPopupStores_success() {
        // given
        PopupStore popupStore1 = PopupStore.builder()
                .id(1L)
                .name("A")
                .description("desc1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .reservationDate(LocalDateTime.now())
                .location(Location.of(37.56826, 126.97794))
                .build();
        PopupStore popupStore2 = PopupStore.builder()
                .id(2L)
                .name("B")
                .description("desc2")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .reservationDate(LocalDateTime.now())
                .location(Location.of(37.56826, 126.97794))
                .build();
        when(popupStoreRepository.findAll()).thenReturn(List.of(popupStore1, popupStore2));

        // when
        List<PopupStoreDto> result = popupStoreService.getAllPopupStores();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(1).getName()).isEqualTo("B");
    }

    @Test
    @DisplayName("팝업스토어 삭제 성공")
    void deletePopupStore_success() {
        // given
        PopupStore popupStore = mock(PopupStore.class);
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.of(popupStore));

        // when
        popupStoreService.deletePopupStore(1L);

        // then
        verify(popupStore).delete();
    }

    @Test
    @DisplayName("팝업스토어 삭제 실패 - 존재하지 않음")
    void deletePopupStore_notFound() {
        // given
        when(popupStoreRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> popupStoreService.deletePopupStore(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않습니다");
    }
}