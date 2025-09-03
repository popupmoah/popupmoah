package com.sgyj.popupmoah.category.application.service;

import com.sgyj.popupmoah.category.domain.aggregate.CategoryAggregate;
import com.sgyj.popupmoah.category.domain.entity.Category;
import com.sgyj.popupmoah.category.domain.port.CategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 애플리케이션 서비스
 * 유스케이스를 구현하는 애플리케이션 레이어
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryApplicationService implements CategoryServicePort {
    
    private final CategoryAggregate aggregate;
    
    @Override
    public Category createCategory(Category category) {
        return aggregate.create(category);
    }
    
    @Override
    public Optional<Category> getCategory(Long id) {
        return aggregate.findById(id);
    }
    
    @Override
    public List<Category> getAllCategories() {
        return aggregate.findAll();
    }
    
    @Override
    public List<Category> getActiveCategories() {
        return aggregate.findActive();
    }
    
    @Override
    public List<Category> getRootCategories() {
        return aggregate.findRootCategories();
    }
    
    @Override
    public Category updateCategory(Long id, Category category) {
        return aggregate.update(id, category);
    }
    
    @Override
    public void deleteCategory(Long id) {
        aggregate.delete(id);
    }
    
    @Override
    public void activateCategory(Long id) {
        aggregate.activate(id);
    }
    
    @Override
    public void deactivateCategory(Long id) {
        aggregate.deactivate(id);
    }
    
    @Override
    public void changeSortOrder(Long id, Double newSortOrder) {
        aggregate.changeSortOrder(id, newSortOrder);
    }
    
    @Override
    public void addChildCategory(Long parentId, Category childCategory) {
        aggregate.addChildCategory(parentId, childCategory);
    }
    
    @Override
    public void removeChildCategory(Long parentId, Long childId) {
        aggregate.removeChildCategory(parentId, childId);
    }
} 