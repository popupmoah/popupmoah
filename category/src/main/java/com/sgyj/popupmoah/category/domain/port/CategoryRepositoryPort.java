package com.sgyj.popupmoah.category.domain.port;

import com.sgyj.popupmoah.category.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 리포지토리 포트
 * 도메인이 정의하는 인터페이스로, 인프라스트럭처가 구현
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
     * 활성화된 카테고리만 조회합니다.
     */
    List<Category> findByActiveTrue();
    
    /**
     * 루트 카테고리만 조회합니다.
     */
    List<Category> findRootCategories();
    
    /**
     * 부모 카테고리로 자식 카테고리들을 조회합니다.
     */
    List<Category> findByParentId(Long parentId);
    
    /**
     * 이름으로 카테고리를 조회합니다.
     */
    Optional<Category> findByName(String name);
    
    /**
     * 카테고리를 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 카테고리가 존재하는지 확인합니다.
     */
    boolean existsById(Long id);
} 