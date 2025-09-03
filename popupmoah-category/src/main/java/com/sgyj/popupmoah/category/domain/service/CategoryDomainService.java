package com.sgyj.popupmoah.category.domain.service;

import com.sgyj.popupmoah.category.domain.entity.Category;
import com.sgyj.popupmoah.category.domain.port.CategoryRepositoryPort;
import com.sgyj.popupmoah.domain.category.port.CategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 도메인 서비스
 * 카테고리 도메인의 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryDomainService implements CategoryServicePort {
    
    private final CategoryRepositoryPort categoryRepositoryPort;
    
    @Override
    public Category createCategory(Category category) {
        // 도메인 규칙 검증
        validateCategory(category);
        
        // 중복 검사
        if (categoryRepositoryPort.existsByName(category.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + category.getName());
        }
        
        // 부모 카테고리 검증
        if (category.getParent() != null) {
            Category parent = categoryRepositoryPort.findById(category.getParent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + category.getParent().getId()));
            category.setParent(parent);
        }
        
        return categoryRepositoryPort.save(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepositoryPort.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepositoryPort.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepositoryPort.findRootCategories();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveRootCategories() {
        return categoryRepositoryPort.findActiveRootCategories();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getChildrenByParentId(Long parentId) {
        return categoryRepositoryPort.findChildrenByParentId(parentId);
    }
    
    @Override
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        
        // 도메인 규칙 검증
        validateCategory(category);
        
        // 이름 중복 검사 (자신 제외)
        if (!existingCategory.getName().equals(category.getName()) && 
            categoryRepositoryPort.existsByName(category.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + category.getName());
        }
        
        // 부모 카테고리 검증 (순환 참조 방지)
        if (category.getParent() != null) {
            validateParentCategory(id, category.getParent().getId());
        }
        
        // 업데이트 로직
        existingCategory.update(
                category.getName(),
                category.getDescription(),
                category.getSortOrder(),
                category.getActive()
        );
        
        if (category.getParent() != null) {
            Category parent = categoryRepositoryPort.findById(category.getParent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + category.getParent().getId()));
            existingCategory.setParent(parent);
        } else {
            existingCategory.setParent(null);
        }
        
        return categoryRepositoryPort.save(existingCategory);
    }
    
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        
        // 자식 카테고리가 있는지 확인
        List<Category> children = categoryRepositoryPort.findChildrenByParentId(id);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("자식 카테고리가 있는 카테고리는 삭제할 수 없습니다. 먼저 자식 카테고리를 삭제하세요.");
        }
        
        categoryRepositoryPort.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepositoryPort.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoryTree() {
        return categoryRepositoryPort.findRootCategories();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getDescendantsByCategoryId(Long categoryId) {
        return categoryRepositoryPort.findAllDescendantsByParentId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getAncestorsByCategoryId(Long categoryId) {
        return categoryRepositoryPort.findAllAncestorsByCategoryId(categoryId);
    }
    
    /**
     * 카테고리 도메인 규칙 검증
     */
    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
        }
        
        if (category.getName().length() > 100) {
            throw new IllegalArgumentException("카테고리 이름은 100자를 초과할 수 없습니다.");
        }
        
        if (category.getSortOrder() != null && category.getSortOrder() < 0) {
            throw new IllegalArgumentException("정렬 순서는 0 이상이어야 합니다.");
        }
    }
    
    /**
     * 부모 카테고리 검증 (순환 참조 방지)
     */
    private void validateParentCategory(Long categoryId, Long parentId) {
        if (categoryId.equals(parentId)) {
            throw new IllegalArgumentException("자신을 부모로 설정할 수 없습니다.");
        }
        
        // 부모의 상위 카테고리들을 확인하여 순환 참조 방지
        List<Category> ancestors = categoryRepositoryPort.findAllAncestorsByCategoryId(parentId);
        for (Category ancestor : ancestors) {
            if (ancestor.getId().equals(categoryId)) {
                throw new IllegalArgumentException("순환 참조가 발생합니다. 자식 카테고리를 부모로 설정할 수 없습니다.");
            }
        }
    }
} 