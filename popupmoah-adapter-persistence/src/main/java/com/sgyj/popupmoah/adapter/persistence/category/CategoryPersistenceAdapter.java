package com.sgyj.popupmoah.adapter.persistence.category;

import com.sgyj.popupmoah.domain.category.entity.Category;
import com.sgyj.popupmoah.domain.category.port.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 영속성 어댑터 (Outbound Adapter)
 * 도메인의 CategoryRepositoryPort를 구현하여 JPA Repository와 연결합니다.
 */
@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {
    
    private final CategoryJpaRepository categoryJpaRepository;
    
    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }
    
    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id);
    }
    
    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAllByOrderBySortOrderAsc();
    }
    
    @Override
    public List<Category> findRootCategories() {
        return categoryJpaRepository.findByParentIsNullOrderBySortOrderAsc();
    }
    
    @Override
    public List<Category> findActiveRootCategories() {
        return categoryJpaRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
    }
    
    @Override
    public List<Category> findChildrenByParentId(Long parentId) {
        return categoryJpaRepository.findById(parentId)
                .map(categoryJpaRepository::findByParentOrderBySortOrderAsc)
                .orElse(List.of());
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return categoryJpaRepository.findByName(name);
    }
    
    @Override
    public boolean existsByName(String name) {
        return categoryJpaRepository.existsByName(name);
    }
    
    @Override
    public List<Category> findAllDescendantsByParentId(Long parentId) {
        return categoryJpaRepository.findAllDescendantsByParentId(parentId);
    }
    
    @Override
    public List<Category> findAllAncestorsByCategoryId(Long categoryId) {
        return categoryJpaRepository.findAllAncestorsByCategoryId(categoryId);
    }
    
    @Override
    public void delete(Category category) {
        categoryJpaRepository.delete(category);
    }
    
    @Override
    public void deleteById(Long id) {
        categoryJpaRepository.deleteById(id);
    }
} 