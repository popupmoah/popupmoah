package com.sgyj.popupmoah.popupstore.adapters.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    List<PopupStoreJpaEntity> findByNameContainingIgnoreCase(String name);

    /**
     * 위치로 팝업스토어를 조회합니다.
     */
    List<PopupStoreJpaEntity> findByLocationContainingIgnoreCase(String location);

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    @Query("SELECT p FROM PopupStoreJpaEntity p WHERE p.active = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :now) AND " +
           "(p.endDate IS NULL OR p.endDate >= :now)")
    List<PopupStoreJpaEntity> findCurrentlyActive(@Param("now") LocalDateTime now);

    /**
     * 복합 검색 조건으로 팝업스토어를 조회합니다.
     */
    @Query("SELECT p FROM PopupStoreJpaEntity p WHERE " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:startDateFrom IS NULL OR p.startDate >= :startDateFrom) AND " +
           "(:startDateTo IS NULL OR p.startDate <= :startDateTo) AND " +
           "(:endDateFrom IS NULL OR p.endDate >= :endDateFrom) AND " +
           "(:endDateTo IS NULL OR p.endDate <= :endDateTo) AND " +
           "(:active IS NULL OR p.active = :active) AND " +
           "(:currentlyActive IS NULL OR " +
           "(:currentlyActive = true AND p.active = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :now) AND " +
           "(p.endDate IS NULL OR p.endDate >= :now)) OR " +
           "(:currentlyActive = false AND (p.active = false OR " +
           "p.startDate > :now OR p.endDate < :now)))")
    Page<PopupStoreJpaEntity> findBySearchConditions(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("location") String location,
            @Param("startDateFrom") LocalDateTime startDateFrom,
            @Param("startDateTo") LocalDateTime startDateTo,
            @Param("endDateFrom") LocalDateTime endDateFrom,
            @Param("endDateTo") LocalDateTime endDateTo,
            @Param("active") Boolean active,
            @Param("currentlyActive") Boolean currentlyActive,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    /**
     * 복합 검색 조건으로 팝업스토어 개수를 조회합니다.
     */
    @Query("SELECT COUNT(p) FROM PopupStoreJpaEntity p WHERE " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:startDateFrom IS NULL OR p.startDate >= :startDateFrom) AND " +
           "(:startDateTo IS NULL OR p.startDate <= :startDateTo) AND " +
           "(:endDateFrom IS NULL OR p.endDate >= :endDateFrom) AND " +
           "(:endDateTo IS NULL OR p.endDate <= :endDateTo) AND " +
           "(:active IS NULL OR p.active = :active) AND " +
           "(:currentlyActive IS NULL OR " +
           "(:currentlyActive = true AND p.active = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :now) AND " +
           "(p.endDate IS NULL OR p.endDate >= :now)) OR " +
           "(:currentlyActive = false AND (p.active = false OR " +
           "p.startDate > :now OR p.endDate < :now)))")
    Long countBySearchConditions(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("location") String location,
            @Param("startDateFrom") LocalDateTime startDateFrom,
            @Param("startDateTo") LocalDateTime startDateTo,
            @Param("endDateFrom") LocalDateTime endDateFrom,
            @Param("endDateTo") LocalDateTime endDateTo,
            @Param("active") Boolean active,
            @Param("currentlyActive") Boolean currentlyActive,
            @Param("now") LocalDateTime now);
} 