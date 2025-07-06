package com.sgyj.popupmoah.module.popupstore.service;

import com.sgyj.popupmoah.module.popupstore.controller.request.CreatePopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import com.sgyj.popupmoah.module.popupstore.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
