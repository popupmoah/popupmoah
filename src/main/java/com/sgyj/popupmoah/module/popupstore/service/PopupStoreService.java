package com.sgyj.popupmoah.module.popupstore.service;

import com.sgyj.popupmoah.module.popupstore.controller.request.CreatePopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopupStoreService {

    private final PopupStoreRepository popupStoreRepository;

    /**
     * 팝업 스토어 등록
     * @param request
     * @return
     */
    public PopupStoreDto createPopupStore(CreatePopupStoreRequest request) {
        PopupStore popupStore = request.mapToEntity();
        popupStoreRepository.save(popupStore);
        return PopupStoreDto.fromEntity(popupStore);
    }

    /**
     * 팝업 스토어 조회
     * @param id
     * @return
     */
    public PopupStoreDto getPopupStore(Long id) {
        PopupStore popupStore = popupStoreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업 스토어가 존재하지 않습니다."));
        return PopupStoreDto.fromEntity(popupStore);
    }

    /**
     * 모든 팝업 스토어 조회
     * @return
     */
    public List<PopupStoreDto> getAllPopupStores() {
        return popupStoreRepository.findAll().stream()
                .map(PopupStoreDto::fromEntity)
                .toList();
    }

    /**
     * 팝업 스토어 삭제
     * @param id
     */
    public void deletePopupStore(Long id) {
        popupStoreRepository.findById(id).ifPresentOrElse(popupStore -> {
            popupStore.delete();
        }, ()-> {
            throw new IllegalArgumentException("팝업 스토어가 존재하지 않습니다.");
        });
    }

}
