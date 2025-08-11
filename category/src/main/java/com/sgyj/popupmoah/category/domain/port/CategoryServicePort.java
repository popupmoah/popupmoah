package com.sgyj.popupmoah.category.domain.port;

import com.sgyj.popupmoah.category.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 서비스 포트
 * 도메인 비즈니스 로직을 위한 인터페이스
 */
public interface CategoryServicePort {
    
    /**
     * 카테고리를 생성합니다.
     */
    Category createCategory(Category category);
    
    /**
     * 카테고리를 조회합니다.
     */
    Optional<Category> getCategory(Long id);
    
    /**
     * 모든 카테고리를 조회합니다.
     */
    List<Category> getAllCategories();
    
    /**
     * 활성화된 카테고리만 조회합니다.
     */
    List<Category> getActiveCategories();
    
    /**
     * 루트 카테고리만 조회합니다.
     */
    List<Category> getRootCategories();
    
    /**
     * 카테고리를 업데이트합니다.
     */
    Category updateCategory(Long id, Category category);
    
    /**
     * 카테고리를 삭제합니다.
     */
    void deleteCategory(Long id);
    
    /**
     * 카테고리를 활성화합니다.
     */
    void activateCategory(Long id);
    
    /**
     * 카테고리를 비활성화합니다.
     */
    void deactivateCategory(Long id);
    
    /**
     * 카테고리 정렬 순서를 변경합니다.
     */
    void changeSortOrder(Long id, Double newSortOrder);
    
    /**
     * 자식 카테고리를 추가합니다.
     */
    void addChildCategory(Long parentId, Category childCategory);
    
    /**
     * 자식 카테고리를 제거합니다.
     */
    void removeChildCategory(Long parentId, Long childId);
} 