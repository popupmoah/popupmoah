package com.sgyj.popupmoah.category.adapters.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 JPA Repository 인터페이스
 * Spring Data JPA를 사용한 데이터 접근
 */
@Repository
public interface CategoryJpaEntityRepository extends JpaRepository<CategoryJpaEntity, Long> {

    /**
     * 활성화된 카테고리를 조회합니다.
     */
    List<CategoryJpaEntity> findByActiveTrue();

    /**
     * 루트 카테고리를 조회합니다.
     */
    List<CategoryJpaEntity> findByParentIsNull();

    /**
     * 부모 카테고리로 자식 카테고리들을 조회합니다.
     */
    List<CategoryJpaEntity> findByParentId(Long parentId);

    /**
     * 이름으로 카테고리를 조회합니다.
     */
    Optional<CategoryJpaEntity> findByName(String name);
} 