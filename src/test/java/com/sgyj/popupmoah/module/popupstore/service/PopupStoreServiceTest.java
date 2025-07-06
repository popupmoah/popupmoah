package com.sgyj.popupmoah.module.popupstore.service;


import com.sgyj.popupmoah.module.popupstore.controller.request.CreatePopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
}