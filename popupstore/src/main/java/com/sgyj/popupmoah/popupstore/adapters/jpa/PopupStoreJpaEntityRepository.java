package com.sgyj.popupmoah.popupstore.adapters.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 팝업스토어 JPA Repository 인터페이스
 * Spring Data JPA를 사용한 데이터 접근
 */
@Repository
public interface PopupStoreJpaEntityRepository extends JpaRepository<PopupStoreJpaEntity, Long> {

    /**
     * 활성화된 팝업스토어를 조회합니다.
     */
    List<PopupStoreJpaEntity> findByActiveTrue();

    /**
     * 카테고리로 팝업스토어를 조회합니다.
     */
    List<PopupStoreJpaEntity> findByCategory(String category);
} 