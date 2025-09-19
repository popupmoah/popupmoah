package com.sgyj.popupmoah.category.domain.repository;

import com.sgyj.popupmoah.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 정렬 순서대로 모든 카테고리를 조회합니다.
     */
    List<Category> findAllByOrderBySortOrderAsc();

    /**
     * 활성화된 카테고리를 정렬 순서대로 조회합니다.
     */
    List<Category> findByActiveTrueOrderBySortOrderAsc();

    /**
     * 루트 카테고리들(부모가 없는 카테고리)을 조회합니다.
     */
    List<Category> findByParentIsNullOrderBySortOrderAsc();

    /**
     * 활성화된 루트 카테고리들을 조회합니다.
     */
    List<Category> findByParentIsNullAndActiveTrueOrderBySortOrderAsc();

    /**
     * 특정 부모 카테고리의 자식 카테고리들을 조회합니다.
     */
    List<Category> findByParentOrderBySortOrderAsc(Category parent);

    /**
     * 특정 부모 카테고리의 활성화된 자식 카테고리들을 조회합니다.
     */
    List<Category> findByParentAndActiveTrueOrderBySortOrderAsc(Category parent);

    /**
     * 이름으로 카테고리를 검색합니다.
     */
    Optional<Category> findByName(String name);

    /**
     * 이름으로 카테고리가 존재하는지 확인합니다.
     */
    boolean existsByName(String name);

    /**
     * 특정 카테고리의 모든 하위 카테고리들을 재귀적으로 조회합니다.
     */
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId OR c.parent.parent.id = :parentId OR c.parent.parent.parent.id = :parentId")
    List<Category> findAllDescendantsByParentId(@Param("parentId") Long parentId);

    /**
     * 특정 카테고리의 모든 상위 카테고리들을 조회합니다.
     */
    @Query("SELECT c FROM Category c WHERE c.id IN (SELECT DISTINCT c2.parent.id FROM Category c2 WHERE c2.id = :categoryId)")
    List<Category> findAllAncestorsByCategoryId(@Param("categoryId") Long categoryId);
} 