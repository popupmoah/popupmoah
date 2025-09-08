package com.sgyj.popupmoah.popupstore.application.service;

import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreResponse;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreSearchRequest;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreSearchResponse;
import com.sgyj.popupmoah.popupstore.domain.aggregate.PopupStoreAggregate;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.annotation.Timed;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 팝업스토어 애플리케이션 서비스
 * 유스케이스를 구현하는 애플리케이션 레이어
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PopupStoreApplicationService implements PopupStoreServicePort {
    
    private final PopupStoreRepositoryPort repository;
    private final PopupStoreAggregate aggregate;
    
    @Override
    @CacheEvict(value = "popupstores", allEntries = true)
    public PopupStore createPopupStore(PopupStore popupStore) {
        log.debug("팝업스토어 생성 - 캐시 무효화");
        return aggregate.create(popupStore);
    }
    
    @Override
    @Cacheable(value = "popupstores", key = "#id")
    @Timed(name = "popupstore.get.by.id", description = "팝업스토어 ID로 조회 시간")
    public Optional<PopupStore> getPopupStore(Long id) {
        log.debug("팝업스토어 조회 (캐시 미스): id={}", id);
        return aggregate.findById(id);
    }
    
    @Override
    @Cacheable(value = "popupstores", key = "'all'")
    @Timed(name = "popupstore.get.all", description = "전체 팝업스토어 조회 시간")
    public List<PopupStore> getAllPopupStores() {
        log.debug("전체 팝업스토어 조회 (캐시 미스)");
        return aggregate.findAll();
    }
    
    @Override
    @Cacheable(value = "popupstores", key = "'active'")
    @Timed(name = "popupstore.get.active", description = "활성 팝업스토어 조회 시간")
    public List<PopupStore> getActivePopupStores() {
        log.debug("활성 팝업스토어 조회 (캐시 미스)");
        return aggregate.findActive();
    }
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "popupstores", key = "#id"),
        @CacheEvict(value = "popupstores", key = "'all'"),
        @CacheEvict(value = "popupstores", key = "'active'")
    })
    @Timed(name = "popupstore.update", description = "팝업스토어 수정 시간")
    public PopupStore updatePopupStore(Long id, PopupStore popupStore) {
        log.debug("팝업스토어 수정 - 캐시 무효화: id={}", id);
        return aggregate.update(id, popupStore);
    }
    
    @Override
    @Timed(name = "popupstore.increment.view", description = "팝업스토어 조회수 증가 시간")
    public void incrementViewCount(Long id) {
        aggregate.incrementViewCount(id);
    }
    
    @Override
    @Timed(name = "popupstore.increment.like", description = "팝업스토어 좋아요 증가 시간")
    public void incrementLikeCount(Long id) {
        aggregate.incrementLikeCount(id);
    }
    
    @Override
    @Timed(name = "popupstore.decrement.like", description = "팝업스토어 좋아요 감소 시간")
    public void decrementLikeCount(Long id) {
        aggregate.decrementLikeCount(id);
    }
    
    /**
     * 팝업스토어를 검색합니다.
     */
    @Transactional(readOnly = true)
    @Timed(name = "popupstore.search", description = "팝업스토어 검색 시간")
    public PopupStoreSearchResponse searchPopupStores(PopupStoreSearchRequest request) {
        log.info("팝업스토어 검색 요청: {}", request);

        // 기본값 설정
        Integer page = request.getPage() != null ? request.getPage() : 0;
        Integer size = request.getSize() != null ? request.getSize() : 20;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";

        // 검색 조건으로 팝업스토어 조회
        List<PopupStore> popupStores = aggregate.searchByConditions(
                request.getKeyword(),
                request.getCategory(),
                request.getLocation(),
                request.getStartDateFrom(),
                request.getStartDateTo(),
                request.getEndDateFrom(),
                request.getEndDateTo(),
                request.getActive(),
                request.getCurrentlyActive(),
                sortBy,
                sortDirection,
                page,
                size
        );

        // 전체 개수 조회
        Long totalElements = aggregate.countByConditions(
                request.getKeyword(),
                request.getCategory(),
                request.getLocation(),
                request.getStartDateFrom(),
                request.getStartDateTo(),
                request.getEndDateFrom(),
                request.getEndDateTo(),
                request.getActive(),
                request.getCurrentlyActive()
        );

        // PopupStoreResponse로 변환
        List<PopupStoreResponse> responses = popupStores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 페이징 정보 계산
        Integer totalPages = (int) Math.ceil((double) totalElements / size);
        Boolean first = page == 0;
        Boolean last = page >= totalPages - 1;

        // 검색 조건 정보
        PopupStoreSearchResponse.SearchCondition searchCondition = PopupStoreSearchResponse.SearchCondition.builder()
                .keyword(request.getKeyword())
                .category(request.getCategory())
                .location(request.getLocation())
                .startDateFrom(request.getStartDateFrom())
                .startDateTo(request.getStartDateTo())
                .endDateFrom(request.getEndDateFrom())
                .endDateTo(request.getEndDateTo())
                .active(request.getActive())
                .currentlyActive(request.getCurrentlyActive())
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return PopupStoreSearchResponse.builder()
                .popupStores(responses)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(size)
                .first(first)
                .last(last)
                .searchCondition(searchCondition)
                .build();
    }

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> searchByName(String name) {
        log.info("이름으로 팝업스토어 검색 요청: name={}", name);
        return aggregate.searchByName(name);
    }

    /**
     * 카테고리로 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> findByCategory(String category) {
        log.info("카테고리로 팝업스토어 조회 요청: category={}", category);
        return aggregate.findByCategory(category);
    }

    /**
     * 위치로 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> findByLocation(String location) {
        log.info("위치로 팝업스토어 조회 요청: location={}", location);
        return aggregate.findByLocation(location);
    }

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> findCurrentlyActive() {
        log.info("현재 진행 중인 팝업스토어 조회 요청");
        return aggregate.findCurrentlyActive();
    }

    /**
     * PopupStore 엔티티를 PopupStoreResponse DTO로 변환
     */
    private PopupStoreResponse convertToResponse(PopupStore popupStore) {
        return PopupStoreResponse.builder()
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

    // ========== 관리자 기능 ==========

    /**
     * 승인 대기 중인 팝업스토어 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<PopupStoreResponse> getPendingPopupStores(Pageable pageable) {
        log.info("승인 대기 팝업스토어 목록 조회 요청");
        
        Page<PopupStore> pendingStores = repository.findByStatus("PENDING", pageable);
        return pendingStores.map(this::convertToResponse);
    }

    /**
     * 팝업스토어 승인
     */
    @Caching(evict = {
        @CacheEvict(value = "popupstores", key = "#popupStoreId"),
        @CacheEvict(value = "popupstores", key = "'all'"),
        @CacheEvict(value = "popupstores", key = "'active'")
    })
    public void approvePopupStore(Long popupStoreId) {
        log.info("팝업스토어 승인 요청: popupStoreId={}", popupStoreId);
        
        Optional<PopupStore> popupStoreOpt = aggregate.findById(popupStoreId);
        if (popupStoreOpt.isEmpty()) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + popupStoreId);
        }
        
        PopupStore popupStore = popupStoreOpt.get();
        if (!"PENDING".equals(popupStore.getStatus())) {
            throw new IllegalArgumentException("승인 대기 상태가 아닌 팝업스토어입니다: " + popupStoreId);
        }
        
        popupStore.approve();
        repository.save(popupStore);
        
        log.info("팝업스토어 승인 완료: popupStoreId={}", popupStoreId);
    }

    /**
     * 팝업스토어 거부
     */
    @Caching(evict = {
        @CacheEvict(value = "popupstores", key = "#popupStoreId"),
        @CacheEvict(value = "popupstores", key = "'all'"),
        @CacheEvict(value = "popupstores", key = "'active'")
    })
    public void rejectPopupStore(Long popupStoreId, String reason) {
        log.info("팝업스토어 거부 요청: popupStoreId={}, reason={}", popupStoreId, reason);
        
        Optional<PopupStore> popupStoreOpt = aggregate.findById(popupStoreId);
        if (popupStoreOpt.isEmpty()) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + popupStoreId);
        }
        
        PopupStore popupStore = popupStoreOpt.get();
        if (!"PENDING".equals(popupStore.getStatus())) {
            throw new IllegalArgumentException("승인 대기 상태가 아닌 팝업스토어입니다: " + popupStoreId);
        }
        
        popupStore.reject(reason);
        repository.save(popupStore);
        
        log.info("팝업스토어 거부 완료: popupStoreId={}, reason={}", popupStoreId, reason);
    }

    /**
     * 팝업스토어 비활성화
     */
    public void deactivatePopupStore(Long popupStoreId) {
        log.info("팝업스토어 비활성화 요청: popupStoreId={}", popupStoreId);
        
        Optional<PopupStore> popupStoreOpt = aggregate.findById(popupStoreId);
        if (popupStoreOpt.isEmpty()) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + popupStoreId);
        }
        
        PopupStore popupStore = popupStoreOpt.get();
        popupStore.deactivate();
        repository.save(popupStore);
        
        log.info("팝업스토어 비활성화 완료: popupStoreId={}", popupStoreId);
    }

    /**
     * 팝업스토어 재활성화
     */
    public void activatePopupStore(Long popupStoreId) {
        log.info("팝업스토어 재활성화 요청: popupStoreId={}", popupStoreId);
        
        Optional<PopupStore> popupStoreOpt = aggregate.findById(popupStoreId);
        if (popupStoreOpt.isEmpty()) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + popupStoreId);
        }
        
        PopupStore popupStore = popupStoreOpt.get();
        popupStore.activate();
        repository.save(popupStore);
        
        log.info("팝업스토어 재활성화 완료: popupStoreId={}", popupStoreId);
    }

    /**
     * 전체 팝업스토어 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public Page<PopupStoreResponse> getAllPopupStoresForAdmin(Pageable pageable, String status) {
        log.info("전체 팝업스토어 목록 조회 (관리자용): status={}", status);
        
        Page<PopupStore> popupStores;
        if (status != null && !status.isEmpty()) {
            popupStores = repository.findByStatus(status, pageable);
        } else {
            popupStores = repository.findAll(pageable);
        }
        
        return popupStores.map(this::convertToResponse);
    }

    /**
     * 팝업스토어 상세 정보 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public PopupStoreResponse getPopupStoreById(Long popupStoreId) {
        log.info("팝업스토어 상세 정보 조회 (관리자용): popupStoreId={}", popupStoreId);
        
        Optional<PopupStore> popupStoreOpt = aggregate.findById(popupStoreId);
        if (popupStoreOpt.isEmpty()) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + popupStoreId);
        }
        
        return convertToResponse(popupStoreOpt.get());
    }

    /**
     * 팝업스토어 삭제 (관리자용)
     */
    public void deletePopupStore(Long popupStoreId) {
        log.info("팝업스토어 삭제 요청 (관리자용): popupStoreId={}", popupStoreId);
        
        if (!repository.existsById(popupStoreId)) {
            throw new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + popupStoreId);
        }
        
        repository.deleteById(popupStoreId);
        
        log.info("팝업스토어 삭제 완료: popupStoreId={}", popupStoreId);
    }
} 