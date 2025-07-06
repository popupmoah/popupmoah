package com.sgyj.popupmoah.module.popupstore.service;


import com.sgyj.popupmoah.module.popupstore.controller.request.CreatePopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PopupStoreServiceTest {

    private final PopupStoreRepository popupStoreRepository = mock(PopupStoreRepository.class);
    private final PopupStoreService popupStoreService = new PopupStoreService(popupStoreRepository);

    @Test
    @DisplayName("createPopupStore saves entity and returns mapped DTO")
    void createPopupStore_savesEntityAndReturnsDto() {
        // given
        CreatePopupStoreRequest request = mock(CreatePopupStoreRequest.class);
        PopupStore popupStore = mock(PopupStore.class);
        PopupStoreDto popupStoreDto = mock(PopupStoreDto.class);

        when(request.mapToEntity()).thenReturn(popupStore);
        when(popupStoreRepository.save(popupStore)).thenReturn(popupStore);

        // PopupStoreDto.fromEntity는 static이므로 실제 팩토리 메서드가 정상 동작한다고 가정
        try (var mocked = mockStatic(PopupStoreDto.class)) {
            mocked.when(() -> PopupStoreDto.fromEntity(popupStore)).thenReturn(popupStoreDto);

            // when
            PopupStoreDto result = popupStoreService.createPopupStore(request);

            // then
            verify(popupStoreRepository).save(popupStore);
            assertThat(result).isEqualTo(popupStoreDto);
        }
    }

    @Test
    @DisplayName("getPopupStore는 id로 조회 후 DTO로 변환해 반환한다")
    void getPopupStore_returnsDto() {
        // given
        Long id = 1L;
        PopupStore popupStore = mock(PopupStore.class);
        PopupStoreDto popupStoreDto = mock(PopupStoreDto.class);

        when(popupStoreRepository.findById(id)).thenReturn(Optional.of(popupStore));
        try (var mocked = mockStatic(PopupStoreDto.class)) {
            mocked.when(() -> PopupStoreDto.fromEntity(popupStore)).thenReturn(popupStoreDto);

            // when
            PopupStoreDto result = popupStoreService.getPopupStore(id);

            // then
            assertThat(result).isEqualTo(popupStoreDto);
        }
    }

    @Test
    @DisplayName("getPopupStore는 존재하지 않으면 예외를 던진다")
    void getPopupStore_throwsExceptionIfNotFound() {
        // given
        Long id = 1L;
        when(popupStoreRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> popupStoreService.getPopupStore(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("팝업 스토어가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("getAllPopupStores는 모든 엔티티를 DTO로 변환해 반환한다")
    void getAllPopupStores_returnsDtoList() {
        // given
        PopupStore popupStore1 = mock(PopupStore.class);
        PopupStore popupStore2 = mock(PopupStore.class);
        PopupStoreDto dto1 = mock(PopupStoreDto.class);
        PopupStoreDto dto2 = mock(PopupStoreDto.class);

        when(popupStoreRepository.findAll()).thenReturn(List.of(popupStore1, popupStore2));
        try (var mocked = mockStatic(PopupStoreDto.class)) {
            mocked.when(() -> PopupStoreDto.fromEntity(popupStore1)).thenReturn(dto1);
            mocked.when(() -> PopupStoreDto.fromEntity(popupStore2)).thenReturn(dto2);

            // when
            List<PopupStoreDto> result = popupStoreService.getAllPopupStores();

            // then
            assertThat(result).containsExactly(dto1, dto2);
        }
    }

    @Test
    @DisplayName("deletePopupStore는 존재하면 delete를 호출한다")
    void deletePopupStore_deletesIfExists() {
        // given
        Long id = 1L;
        PopupStore popupStore = mock(PopupStore.class);
        when(popupStoreRepository.findById(id)).thenReturn(Optional.of(popupStore));

        // when
        popupStoreService.deletePopupStore(id);

        // then
        verify(popupStore).delete();

    }

    @Test
    @DisplayName("deletePopupStore는 존재하지 않으면 예외를 던진다")
    void deletePopupStore_throwsExceptionIfNotFound() {
        // given
        Long id = 1L;
        when(popupStoreRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> popupStoreService.deletePopupStore(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("팝업 스토어가 존재하지 않습니다.");
    }

}