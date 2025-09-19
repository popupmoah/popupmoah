package com.sgyj.popupmoah.popupstore.infrastructure.persistence;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 Repository 어댑터
 * 기술 중립적인 어댑터로, 구체적인 기술 구현체를 선택할 수 있음
 */
@Repository
@RequiredArgsConstructor
public class PopupStoreRepositoryAdapter implements PopupStoreRepositoryPort {
    
    // 기술별 구현체를 주입받음 (JPA, R2DBC 등)
    private final com.sgyj.popupmoah.popupstore.adapters.jpa.PopupStoreJpaRepository jpaRepository;
    // private final PopupStoreR2dbcRepository r2dbcRepository; // R2DBC 사용 시
    
    @Override
    public PopupStore save(PopupStore popupStore) {
        // 현재는 JPA 사용, 필요시 R2DBC로 변경 가능
        return jpaRepository.save(popupStore);
        // return r2dbcRepository.save(popupStore); // R2DBC 사용 시
    }
    
    @Override
    public Optional<PopupStore> findById(Long id) {
        return jpaRepository.findById(id);
        // return r2dbcRepository.findById(id); // R2DBC 사용 시
    }
    
    @Override
    public List<PopupStore> findAll() {
        return jpaRepository.findAll();
        // return r2dbcRepository.findAll(); // R2DBC 사용 시
    }
    
    @Override
    public List<PopupStore> findByActiveTrue() {
        return jpaRepository.findByActiveTrue();
        // return r2dbcRepository.findByActiveTrue(); // R2DBC 사용 시
    }
    
    @Override
    public List<PopupStore> findByCategory(String category) {
        return jpaRepository.findByCategory(category);
        // return r2dbcRepository.findByCategory(category); // R2DBC 사용 시
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

    @Override
    public List<PopupStore> findByNameContaining(String name) {
        return jpaRepository.findByNameContaining(name);
        // return r2dbcRepository.findByNameContaining(name); // R2DBC 사용 시
    }

    @Override
    public List<PopupStore> findByLocation(String location) {
        return jpaRepository.findByLocation(location);
        // return r2dbcRepository.findByLocation(location); // R2DBC 사용 시
    }

    @Override
    public List<PopupStore> findCurrentlyActive() {
        return jpaRepository.findCurrentlyActive();
        // return r2dbcRepository.findCurrentlyActive(); // R2DBC 사용 시
    }

    @Override
    public List<PopupStore> findBySearchConditions(String keyword, String category, String location,
                                                   LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                                   LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                                   Boolean active, Boolean currentlyActive,
                                                   String sortBy, String sortDirection,
                                                   Integer page, Integer size) {
        return jpaRepository.findBySearchConditions(keyword, category, location,
                startDateFrom, startDateTo, endDateFrom, endDateTo,
                active, currentlyActive, sortBy, sortDirection, page, size);
        // return r2dbcRepository.findBySearchConditions(...); // R2DBC 사용 시
    }

    @Override
    public Long countBySearchConditions(String keyword, String category, String location,
                                       LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                       LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                       Boolean active, Boolean currentlyActive) {
        return jpaRepository.countBySearchConditions(keyword, category, location,
                startDateFrom, startDateTo, endDateFrom, endDateTo,
                active, currentlyActive);
        // return r2dbcRepository.countBySearchConditions(...); // R2DBC 사용 시
    }

    @Override
    public Page<PopupStore> findByStatus(String status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable);
        // return r2dbcRepository.findByStatus(status, pageable); // R2DBC 사용 시
    }

    @Override
    public Page<PopupStore> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
        // return r2dbcRepository.findAll(pageable); // R2DBC 사용 시
    }
} 