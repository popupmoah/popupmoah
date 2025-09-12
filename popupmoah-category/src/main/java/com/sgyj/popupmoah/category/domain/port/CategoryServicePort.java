package com.sgyj.popupmoah.category.domain.port;

import com.sgyj.popupmoah.category.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 서비스 포트 (Inbound Port)
 * 외부에서 카테고리 도메인 서비스를 호출할 수 있는 인터페이스를 정의합니다.
 */
public interface CategoryServicePort {
    
    /**
     * 카테고리를 생성합니다.
     */
    Category createCategory(Category category);
    
    /**
     * ID로 카테고리를 조회합니다.
     */
    Optional<Category> getCategoryById(Long id);
    
    /**
     * 모든 카테고리를 조회합니다.
     */
    List<Category> getAllCategories();
    
    /**
     * 루트 카테고리들을 조회합니다.
     */
    List<Category> getRootCategories();
    
    /**
     * 활성화된 루트 카테고리들을 조회합니다.
     */
    List<Category> getActiveRootCategories();
    
    /**
     * 특정 부모 카테고리의 자식 카테고리들을 조회합니다.
     */
    List<Category> getChildrenByParentId(Long parentId);
    
    /**
     * 카테고리를 업데이트합니다.
     */
    Category updateCategory(Long id, Category category);
    
    /**
     * 카테고리를 삭제합니다.
     */
    void deleteCategory(Long id);
    
    /**
     * 이름으로 카테고리가 존재하는지 확인합니다.
     */
    boolean existsByName(String name);
    
    /**
     * 카테고리 트리 구조를 조회합니다.
     */
    List<Category> getCategoryTree();
    
    /**
     * 특정 카테고리의 모든 하위 카테고리들을 조회합니다.
     */
    List<Category> getDescendantsByCategoryId(Long categoryId);
    
    /**
     * 특정 카테고리의 모든 상위 카테고리들을 조회합니다.
     */
    List<Category> getAncestorsByCategoryId(Long categoryId);
} 