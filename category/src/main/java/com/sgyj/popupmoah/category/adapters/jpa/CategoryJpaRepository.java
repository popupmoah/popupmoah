package com.sgyj.popupmoah.category.adapters.jpa;

import com.sgyj.popupmoah.category.domain.entity.Category;
import com.sgyj.popupmoah.category.domain.port.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 JPA Repository 어댑터
 * 포트 인터페이스를 구현하는 인프라스트럭처 어댑터
 */
@Repository
@RequiredArgsConstructor
public class CategoryJpaRepository implements CategoryRepositoryPort {
    
    private final CategoryJpaEntityRepository repository;
    
    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = toJpaEntity(category);
        CategoryJpaEntity savedEntity = repository.save(entity);
        return toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<Category> findById(Long id) {
        return repository.findById(id)
                .map(this::toDomainEntity);
    }
    
    @Override
    public List<Category> findAll() {
        return repository.findAll().stream()
                .map(this::toDomainEntity)
                .toList();
    }
    
    @Override
    public List<Category> findByActiveTrue() {
        return repository.findByActiveTrue().stream()
                .map(this::toDomainEntity)
                .toList();
    }
    
    @Override
    public List<Category> findRootCategories() {
        return repository.findByParentIsNull().stream()
                .map(this::toDomainEntity)
                .toList();
    }
    
    @Override
    public List<Category> findByParentId(Long parentId) {
        return repository.findByParentId(parentId).stream()
                .map(this::toDomainEntity)
                .toList();
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return repository.findByName(name)
                .map(this::toDomainEntity);
    }
    
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
    
    /**
     * 도메인 엔티티를 JPA 엔티티로 변환
     */
    private CategoryJpaEntity toJpaEntity(Category category) {
        return CategoryJpaEntity.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .sortOrder(category.getSortOrder())
                .parent(category.getParent() != null ? toJpaEntity(category.getParent()) : null)
                .build();
    }
    
    /**
     * JPA 엔티티를 도메인 엔티티로 변환
     */
    private Category toDomainEntity(CategoryJpaEntity entity) {
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .sortOrder(entity.getSortOrder())
                .parent(entity.getParent() != null ? toDomainEntity(entity.getParent()) : null)
                .build();
    }
} 