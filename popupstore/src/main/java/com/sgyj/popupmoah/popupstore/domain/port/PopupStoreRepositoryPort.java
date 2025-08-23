package com.sgyj.popupmoah.popupstore.domain.port;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 리포지토리 포트
 * 도메인이 정의하는 인터페이스로, 인프라스트럭처가 구현
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
     * 활성화된 팝업스토어만 조회합니다.
     */
    List<PopupStore> findByActiveTrue();
    
    /**
     * 카테고리로 팝업스토어를 조회합니다.
     */
    List<PopupStore> findByCategory(String category);
    
    /**
     * 팝업스토어를 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 팝업스토어가 존재하는지 확인합니다.
     */
    boolean existsById(Long id);

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    List<PopupStore> findByNameContaining(String name);

    /**
     * 위치로 팝업스토어를 조회합니다.
     */
    List<PopupStore> findByLocation(String location);

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    List<PopupStore> findCurrentlyActive();

    /**
     * 복합 검색 조건으로 팝업스토어를 조회합니다.
     */
    List<PopupStore> findBySearchConditions(String keyword, String category, String location, 
                                           LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                           LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                           Boolean active, Boolean currentlyActive,
                                           String sortBy, String sortDirection,
                                           Integer page, Integer size);

    /**
     * 복합 검색 조건으로 팝업스토어 개수를 조회합니다.
     */
    Long countBySearchConditions(String keyword, String category, String location,
                                LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                Boolean active, Boolean currentlyActive);
} 