package com.sgyj.popupmoah.popupstore.domain.aggregate;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 팝업스토어 어그리게이트 루트
 * 팝업스토어 도메인에 대한 접근을 제어하는 루트 엔티티
 */
public class PopupStoreAggregate {
    
    private final PopupStoreRepositoryPort repository;
    
    public PopupStoreAggregate(PopupStoreRepositoryPort repository) {
        this.repository = repository;
    }
    
    /**
     * 팝업스토어를 생성합니다.
     */
    public PopupStore create(PopupStore popupStore) {
        // 도메인 규칙 검증
        validatePopupStore(popupStore);
        return repository.save(popupStore);
    }
    
    /**
     * 팝업스토어를 조회합니다.
     */
    public Optional<PopupStore> findById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * 모든 팝업스토어를 조회합니다.
     */
    public List<PopupStore> findAll() {
        return repository.findAll();
    }
    
    /**
     * 활성화된 팝업스토어만 조회합니다.
     */
    public List<PopupStore> findActive() {
        return repository.findByActiveTrue();
    }
    
    /**
     * 팝업스토어를 업데이트합니다.
     */
    public PopupStore update(Long id, PopupStore popupStore) {
        Optional<PopupStore> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + id);
        }
        
        // 도메인 규칙 검증
        validatePopupStore(popupStore);
        
        PopupStore updated = existing.get();
        // 불변성을 유지하면서 업데이트
        updated = PopupStore.builder()
                .id(updated.getId())
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
        
        return repository.save(updated);
    }
    
    /**
     * 팝업스토어를 삭제합니다.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + id);
        }
        repository.deleteById(id);
    }
    
    /**
     * 조회수를 증가시킵니다.
     */
    public void incrementViewCount(Long id) {
        Optional<PopupStore> popupStore = repository.findById(id);
        if (popupStore.isPresent()) {
            PopupStore store = popupStore.get();
            store.incrementViewCount();
            repository.save(store);
        }
    }
    
    /**
     * 좋아요 수를 증가시킵니다.
     */
    public void incrementLikeCount(Long id) {
        Optional<PopupStore> popupStore = repository.findById(id);
        if (popupStore.isPresent()) {
            PopupStore store = popupStore.get();
            store.incrementLikeCount();
            repository.save(store);
        }
    }
    
    /**
     * 좋아요 수를 감소시킵니다.
     */
    public void decrementLikeCount(Long id) {
        Optional<PopupStore> popupStore = repository.findById(id);
        if (popupStore.isPresent()) {
            PopupStore store = popupStore.get();
            store.decrementLikeCount();
            repository.save(store);
        }
    }
    
    /**
     * 팝업스토어를 활성화합니다.
     */
    public void activate(Long id) {
        Optional<PopupStore> popupStore = repository.findById(id);
        if (popupStore.isPresent()) {
            PopupStore store = popupStore.get();
            store.activate();
            repository.save(store);
        }
    }
    
    /**
     * 팝업스토어를 비활성화합니다.
     */
    public void deactivate(Long id) {
        Optional<PopupStore> popupStore = repository.findById(id);
        if (popupStore.isPresent()) {
            PopupStore store = popupStore.get();
            store.deactivate();
            repository.save(store);
        }
    }
    
    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    public List<PopupStore> searchByName(String name) {
        return repository.findByNameContaining(name);
    }

    /**
     * 카테고리로 팝업스토어를 조회합니다.
     */
    public List<PopupStore> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    /**
     * 위치로 팝업스토어를 조회합니다.
     */
    public List<PopupStore> findByLocation(String location) {
        return repository.findByLocation(location);
    }

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    public List<PopupStore> findCurrentlyActive() {
        return repository.findCurrentlyActive();
    }

    /**
     * 복합 검색 조건으로 팝업스토어를 조회합니다.
     */
    public List<PopupStore> searchByConditions(String keyword, String category, String location,
                                               LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                               LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                               Boolean active, Boolean currentlyActive,
                                               String sortBy, String sortDirection,
                                               Integer page, Integer size) {
        return repository.findBySearchConditions(keyword, category, location,
                startDateFrom, startDateTo, endDateFrom, endDateTo,
                active, currentlyActive, sortBy, sortDirection, page, size);
    }

    /**
     * 복합 검색 조건으로 팝업스토어 개수를 조회합니다.
     */
    public Long countByConditions(String keyword, String category, String location,
                                 LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                 LocalDateTime endDateFrom, LocalDateTime endDateTo,
                                 Boolean active, Boolean currentlyActive) {
        return repository.countBySearchConditions(keyword, category, location,
                startDateFrom, startDateTo, endDateFrom, endDateTo,
                active, currentlyActive);
    }

    /**
     * 팝업스토어 도메인 규칙을 검증합니다.
     */
    private void validatePopupStore(PopupStore popupStore) {
        if (popupStore.getName() == null || popupStore.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("팝업스토어 이름은 필수입니다.");
        }
        
        if (popupStore.getStartDate() != null && popupStore.getEndDate() != null) {
            if (popupStore.getStartDate().isAfter(popupStore.getEndDate())) {
                throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
            }
        }
    }
} 