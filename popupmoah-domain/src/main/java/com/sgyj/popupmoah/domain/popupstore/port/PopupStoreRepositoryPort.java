package com.sgyj.popupmoah.domain.popupstore.port;

import com.sgyj.popupmoah.domain.popupstore.entity.PopupStore;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 저장소 포트 (Outbound Port)
 * 도메인에서 외부 저장소와의 인터페이스를 정의합니다.
 */
public interface PopupStoreRepositoryPort {
    
    /**
     * 팝업스토어를 저장합니다.
     */
    PopupStore save(PopupStore popupStore);
    
    /**
     * ID로 팝업스토어를 조회합니다.
     */
    Optional<PopupStore> findById(Long id);
    
    /**
     * 모든 팝업스토어를 조회합니다.
     */
    List<PopupStore> findAll();
    
    /**
     * 이름으로 팝업스토어가 존재하는지 확인합니다.
     */
    boolean existsByName(String name);
    
    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    Optional<PopupStore> findByName(String name);
    
    /**
     * 팝업스토어를 삭제합니다.
     */
    void delete(PopupStore popupStore);
    
    /**
     * ID로 팝업스토어를 삭제합니다.
     */
    void deleteById(Long id);
} 