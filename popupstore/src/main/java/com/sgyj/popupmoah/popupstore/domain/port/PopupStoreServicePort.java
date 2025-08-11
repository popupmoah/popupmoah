package com.sgyj.popupmoah.popupstore.domain.port;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 서비스 포트
 * 도메인 비즈니스 로직을 위한 인터페이스
 */
public interface PopupStoreServicePort {
    
    /**
     * 팝업스토어를 생성합니다.
     */
    PopupStore createPopupStore(PopupStore popupStore);
    
    /**
     * 팝업스토어를 조회합니다.
     */
    Optional<PopupStore> getPopupStore(Long id);
    
    /**
     * 모든 팝업스토어를 조회합니다.
     */
    List<PopupStore> getAllPopupStores();
    
    /**
     * 활성화된 팝업스토어만 조회합니다.
     */
    List<PopupStore> getActivePopupStores();
    
    /**
     * 팝업스토어를 업데이트합니다.
     */
    PopupStore updatePopupStore(Long id, PopupStore popupStore);
    
    /**
     * 팝업스토어를 삭제합니다.
     */
    void deletePopupStore(Long id);
    
    /**
     * 팝업스토어 조회수를 증가시킵니다.
     */
    void incrementViewCount(Long id);
    
    /**
     * 팝업스토어 좋아요 수를 증가시킵니다.
     */
    void incrementLikeCount(Long id);
    
    /**
     * 팝업스토어 좋아요 수를 감소시킵니다.
     */
    void decrementLikeCount(Long id);
    
    /**
     * 팝업스토어를 활성화합니다.
     */
    void activatePopupStore(Long id);
    
    /**
     * 팝업스토어를 비활성화합니다.
     */
    void deactivatePopupStore(Long id);
} 