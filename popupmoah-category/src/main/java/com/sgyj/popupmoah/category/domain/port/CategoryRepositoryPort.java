package com.sgyj.popupmoah.category.domain.port;

import com.sgyj.popupmoah.category.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 저장소 포트 (Outbound Port)
 * 도메인에서 외부 저장소와의 인터페이스를 정의합니다.
 */
public interface CategoryRepositoryPort {
    
    /**
     * 카테고리를 저장합니다.
     */
    Category save(Category category);
    
    /**
     * ID로 카테고리를 조회합니다.
     */
    Optional<Category> findById(Long id);
    
    /**
     * 모든 카테고리를 조회합니다.
     */
    List<Category> findAll();
    
    /**
     * 루트 카테고리들을 조회합니다.
     */
    List<Category> findRootCategories();
    
    /**
     * 활성화된 루트 카테고리들을 조회합니다.
     */
    List<Category> findActiveRootCategories();
    
    /**
     * 특정 부모 카테고리의 자식 카테고리들을 조회합니다.
     */
    List<Category> findChildrenByParentId(Long parentId);
    
    /**
     * 이름으로 카테고리를 검색합니다.
     */
    Optional<Category> findByName(String name);
    
    /**
     * 이름으로 카테고리가 존재하는지 확인합니다.
     */
    boolean existsByName(String name);
    
    /**
     * 특정 카테고리의 모든 하위 카테고리들을 조회합니다.
     */
    List<Category> findAllDescendantsByParentId(Long parentId);
    
    /**
     * 특정 카테고리의 모든 상위 카테고리들을 조회합니다.
     */
    List<Category> findAllAncestorsByCategoryId(Long categoryId);
    
    /**
     * 카테고리를 삭제합니다.
     */
    void delete(Category category);
    
    /**
     * ID로 카테고리를 삭제합니다.
     */
    void deleteById(Long id);
} 