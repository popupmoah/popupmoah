package com.sgyj.popupmoah.popupstore.infrastructure.persistence;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 팝업스토어 JPA Repository
 * 팝업스토어 엔티티의 데이터 접근을 담당합니다.
 */
@Repository
public interface PopupStoreJpaRepository extends JpaRepository<PopupStore, Long> {

    /**
     * 활성화된 팝업스토어를 조회합니다.
     */
    List<PopupStore> findByActiveTrue();

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    @Query("SELECT p FROM PopupStore p WHERE p.active = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :now) AND " +
           "(p.endDate IS NULL OR p.endDate >= :now)")
    List<PopupStore> findCurrentlyActive(@Param("now") LocalDateTime now);

    /**
     * 카테고리별 팝업스토어를 조회합니다.
     */
    List<PopupStore> findByCategoryAndActiveTrue(String category);

    /**
     * 위치별 팝업스토어를 조회합니다.
     */
    List<PopupStore> findByLocationAndActiveTrue(String location);

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    List<PopupStore> findByNameContainingAndActiveTrue(String name);

    /**
     * 소스 URL로 팝업스토어가 존재하는지 확인합니다.
     */
    boolean existsBySourceUrl(String sourceUrl);

    /**
     * 모든 소스 URL을 조회합니다.
     */
    @Query("SELECT p.sourceUrl FROM PopupStore p WHERE p.sourceUrl IS NOT NULL")
    List<String> findAllSourceUrls();
} 