package com.sgyj.popupmoah.category.domain.aggregate;

import com.sgyj.popupmoah.category.domain.entity.Category;
import com.sgyj.popupmoah.category.domain.port.CategoryRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 어그리게이트 루트
 * 카테고리 도메인에 대한 접근을 제어하는 루트 엔티티
 */
public class CategoryAggregate {
    
    private final CategoryRepositoryPort repository;
    
    public CategoryAggregate(CategoryRepositoryPort repository) {
        this.repository = repository;
    }
    
    /**
     * 카테고리를 생성합니다.
     */
    public Category create(Category category) {
        // 도메인 규칙 검증
        validateCategory(category);
        return repository.save(category);
    }
    
    /**
     * 카테고리를 조회합니다.
     */
    public Optional<Category> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * 모든 카테고리를 조회합니다.
     */
    public List<Category> findAll() {
        return repository.findAll();
    }
    
    /**
     * 활성화된 카테고리만 조회합니다.
     */
    public List<Category> findActive() {
        return repository.findByActiveTrue();
    }
    
    /**
     * 루트 카테고리만 조회합니다.
     */
    public List<Category> findRootCategories() {
        return repository.findRootCategories();
    }
    
    /**
     * 카테고리를 업데이트합니다.
     */
    public Category update(Long id, Category category) {
        Optional<Category> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id);
        }
        
        // 도메인 규칙 검증
        validateCategory(category);
        
        Category updated = existing.get();
        // 불변성을 유지하면서 업데이트
        updated = Category.builder()
                .id(updated.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .sortOrder(category.getSortOrder())
                .parent(category.getParent())
                .children(category.getChildren())
                .popupStores(category.getPopupStores())
                .build();
        
        return repository.save(updated);
    }
    
    /**
     * 카테고리를 삭제합니다.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id);
        }
        repository.deleteById(id);
    }
    
    /**
     * 카테고리를 활성화합니다.
     */
    public void activate(Long id) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent()) {
            Category cat = category.get();
            cat.toggleActive();
            repository.save(cat);
        }
    }
    
    /**
     * 카테고리를 비활성화합니다.
     */
    public void deactivate(Long id) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent()) {
            Category cat = category.get();
            if (cat.isActive()) {
                cat.toggleActive();
                repository.save(cat);
            }
        }
    }
    
    /**
     * 카테고리 정렬 순서를 변경합니다.
     */
    public void changeSortOrder(Long id, Double newSortOrder) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent()) {
            Category cat = category.get();
            cat.changeSortOrder(newSortOrder);
            repository.save(cat);
        }
    }
    
    /**
     * 자식 카테고리를 추가합니다.
     */
    public void addChildCategory(Long parentId, Category childCategory) {
        Optional<Category> parent = repository.findById(parentId);
        if (parent.isPresent()) {
            Category parentCat = parent.get();
            parentCat.addChild(childCategory);
            repository.save(parentCat);
            repository.save(childCategory);
        }
    }
    
    /**
     * 자식 카테고리를 제거합니다.
     */
    public void removeChildCategory(Long parentId, Long childId) {
        Optional<Category> parent = repository.findById(parentId);
        Optional<Category> child = repository.findById(childId);
        
        if (parent.isPresent() && child.isPresent()) {
            Category parentCat = parent.get();
            Category childCat = child.get();
            parentCat.removeChild(childCat);
            repository.save(parentCat);
            repository.save(childCat);
        }
    }
    
    /**
     * 카테고리 도메인 규칙을 검증합니다.
     */
    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
        }
        
        if (category.getSortOrder() != null && category.getSortOrder() < 0) {
            throw new IllegalArgumentException("정렬 순서는 0 이상이어야 합니다.");
        }
    }
} 