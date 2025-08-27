package com.sgyj.popupmoah.popupstore.adapters.jpa;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 JPA Repository 어댑터
 * 포트 인터페이스를 구현하는 인프라스트럭처 어댑터
 */
@Repository
@RequiredArgsConstructor
public class PopupStoreJpaRepository implements PopupStoreRepositoryPort {
    
    private final PopupStoreJpaEntityRepository repository;
    
    @Override
    public PopupStore save(PopupStore popupStore) {
        PopupStoreJpaEntity entity = toJpaEntity(popupStore);
        PopupStoreJpaEntity savedEntity = repository.save(entity);
        return toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<PopupStore> findById(Long id) {
        return repository.findById(id)
                .map(this::toDomainEntity);
    }
    
    @Override
    public List<PopupStore> findAll() {
        return repository.findAll().stream()
                .map(this::toDomainEntity)
                .toList();
    }
    
    @Override
    public List<PopupStore> findByActiveTrue() {
        return repository.findByActiveTrue().stream()
                .map(this::toDomainEntity)
                .toList();
    }
    
    @Override
    public List<PopupStore> findByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(this::toDomainEntity)
                .toList();
    }
    
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public List<PopupStore> findByNameContaining(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public List<PopupStore> findByLocation(String location) {
        return repository.findByLocationContainingIgnoreCase(location).stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public List<PopupStore> findCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return repository.findCurrentlyActive(now).stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public List<PopupStore> findBySearchConditions(String keyword, String category, String location,
                                                   LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                                   LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                                   Boolean active, Boolean currentlyActive,
                                                   String sortBy, String sortDirection,
                                                   Integer page, Integer size) {
        // 정렬 설정
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        LocalDateTime now = LocalDateTime.now();
        return repository.findBySearchConditions(
                keyword, category, location,
                startDateFrom, startDateTo, endDateFrom, endDateTo,
                active, currentlyActive, now, pageable
        ).getContent().stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public Long countBySearchConditions(String keyword, String category, String location,
                                       LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                       LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                       Boolean active, Boolean currentlyActive) {
        LocalDateTime now = LocalDateTime.now();
        return repository.countBySearchConditions(
                keyword, category, location,
                startDateFrom, startDateTo, endDateFrom, endDateTo,
                active, currentlyActive, now
        );
    }

    // ========== 관리자 기능 ==========

    @Override
    public Page<PopupStore> findByStatus(String status, Pageable pageable) {
        Page<PopupStoreJpaEntity> entities = repository.findByStatus(status, pageable);
        return entities.map(this::toDomainEntity);
    }

    @Override
    public Page<PopupStore> findAll(Pageable pageable) {
        Page<PopupStoreJpaEntity> entities = repository.findAll(pageable);
        return entities.map(this::toDomainEntity);
    }
    
    /**
     * 도메인 엔티티를 JPA 엔티티로 변환
     */
    private PopupStoreJpaEntity toJpaEntity(PopupStore popupStore) {
        return PopupStoreJpaEntity.builder()
                .id(popupStore.getId())
                .name(popupStore.getName())
                .description(popupStore.getDescription())
                .imageUrl(popupStore.getImageUrl())
                .sourceUrl(popupStore.getSourceUrl())
                .category(popupStore.getCategory())
                .startDate(popupStore.getStartDate())
                .endDate(popupStore.getEndDate())
                .location(popupStore.getLocation())
                .address(popupStore.getAddress())
                .latitude(popupStore.getLatitude())
                .longitude(popupStore.getLongitude())
                .active(popupStore.getActive())
                .status(popupStore.getStatus())
                .rejectionReason(popupStore.getRejectionReason())
                .viewCount(popupStore.getViewCount())
                .likeCount(popupStore.getLikeCount())
                .createdAt(popupStore.getCreatedAt())
                .updatedAt(popupStore.getUpdatedAt())
                .build();
    }
    
    /**
     * JPA 엔티티를 도메인 엔티티로 변환
     */
    private PopupStore toDomainEntity(PopupStoreJpaEntity entity) {
        return PopupStore.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .sourceUrl(entity.getSourceUrl())
                .category(entity.getCategory())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .location(entity.getLocation())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .active(entity.getActive())
                .status(entity.getStatus())
                .rejectionReason(entity.getRejectionReason())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
} 