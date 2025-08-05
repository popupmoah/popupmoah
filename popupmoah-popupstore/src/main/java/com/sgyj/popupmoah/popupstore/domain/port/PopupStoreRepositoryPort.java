package com.sgyj.popupmoah.popupstore.domain.port;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;

import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 저장소 포트 (Outbound Port)
 * 도메인에서 외부 저장소와의 상호작용을 위한 인터페이스입니다.
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
     * 모든 활성화된 팝업스토어를 조회합니다.
     */
    List<PopupStore> findAllActive();

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    List<PopupStore> findCurrentlyActive();

    /**
     * 카테고리별 팝업스토어를 조회합니다.
     */
    List<PopupStore> findByCategory(String category);

    /**
     * 위치별 팝업스토어를 조회합니다.
     */
    List<PopupStore> findByLocation(String location);

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    List<PopupStore> findByNameContaining(String name);

    /**
     * 팝업스토어를 삭제합니다.
     */
    void deleteById(Long id);

    /**
     * 소스 URL로 팝업스토어가 존재하는지 확인합니다.
     */
    boolean existsBySourceUrl(String sourceUrl);

    /**
     * 모든 소스 URL을 조회합니다.
     */
    List<String> findAllSourceUrls();
} 