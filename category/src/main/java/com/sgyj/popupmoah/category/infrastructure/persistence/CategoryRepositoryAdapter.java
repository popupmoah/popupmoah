package com.sgyj.popupmoah.category.infrastructure.persistence;

import com.sgyj.popupmoah.category.domain.entity.Category;
import com.sgyj.popupmoah.category.domain.port.CategoryRepositoryPort;
import com.sgyj.popupmoah.category.adapters.jpa.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 Repository 어댑터
 * 기술 중립적인 어댑터로, 구체적인 기술 구현체를 선택할 수 있음
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {
    
    // 기술별 구현체를 주입받음 (JPA, R2DBC 등)
    private final CategoryJpaRepository jpaRepository;
    // private final CategoryR2dbcRepository r2dbcRepository; // R2DBC 사용 시
    
    @Override
    public Category save(Category category) {
        // 현재는 JPA 사용, 필요시 R2DBC로 변경 가능
        return jpaRepository.save(category);
        // return r2dbcRepository.save(category); // R2DBC 사용 시
    }
    
    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id);
        // return r2dbcRepository.findById(id); // R2DBC 사용 시
    }
    
    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll();
        // return r2dbcRepository.findAll(); // R2DBC 사용 시
    }
    
    @Override
    public List<Category> findByActiveTrue() {
        return jpaRepository.findByActiveTrue();
        // return r2dbcRepository.findByActiveTrue(); // R2DBC 사용 시
    }
    
    @Override
    public List<Category> findRootCategories() {
        return jpaRepository.findRootCategories();
        // return r2dbcRepository.findRootCategories(); // R2DBC 사용 시
    }
    
    @Override
    public List<Category> findByParentId(Long parentId) {
        return jpaRepository.findByParentId(parentId);
        // return r2dbcRepository.findByParentId(parentId); // R2DBC 사용 시
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name);
        // return r2dbcRepository.findByName(name); // R2DBC 사용 시
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
        // r2dbcRepository.deleteById(id); // R2DBC 사용 시
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
        // return r2dbcRepository.existsById(id); // R2DBC 사용 시
    }
} 