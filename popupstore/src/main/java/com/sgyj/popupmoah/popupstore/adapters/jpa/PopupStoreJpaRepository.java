package com.sgyj.popupmoah.popupstore.adapters.jpa;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
                .active(popupStore.getActive())
                .viewCount(popupStore.getViewCount())
                .likeCount(popupStore.getLikeCount())
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
                .active(entity.getActive())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .build();
    }
} 