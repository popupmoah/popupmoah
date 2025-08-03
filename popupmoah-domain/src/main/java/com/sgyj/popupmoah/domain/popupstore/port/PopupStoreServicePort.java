package com.sgyj.popupmoah.domain.popupstore.port;

import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 서비스 포트 (Inbound Port)
 * 외부에서 도메인 서비스를 호출할 수 있는 인터페이스를 정의합니다.
 */
public interface PopupStoreServicePort {
    
    /**
     * 팝업스토어를 생성합니다.
     */
    PopupStore createPopupStore(PopupStore popupStore);
    
    /**
     * ID로 팝업스토어를 조회합니다.
     */
    Optional<PopupStore> getPopupStoreById(Long id);
    
    /**
     * 모든 팝업스토어를 조회합니다.
     */
    List<PopupStore> getAllPopupStores();
    
    /**
     * 팝업스토어를 업데이트합니다.
     */
    PopupStore updatePopupStore(Long id, PopupStore popupStore);
    
    /**
     * 팝업스토어를 삭제합니다.
     */
    void deletePopupStore(Long id);
    
    /**
     * 이름으로 팝업스토어가 존재하는지 확인합니다.
     */
    boolean existsByName(String name);
} 