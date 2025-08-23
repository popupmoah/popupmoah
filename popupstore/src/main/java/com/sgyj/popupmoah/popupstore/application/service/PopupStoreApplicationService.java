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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PopupStore createPopupStore(PopupStore popupStore) {
        return aggregate.create(popupStore);
    }
    
    @Override
    public Optional<PopupStore> getPopupStore(Long id) {
        return aggregate.findById(id);
    }
    
    @Override
    public List<PopupStore> getAllPopupStores() {
        return aggregate.findAll();
    }
    
    @Override
    public List<PopupStore> getActivePopupStores() {
        return aggregate.findActive();
    }
    
    @Override
    public PopupStore updatePopupStore(Long id, PopupStore popupStore) {
        return aggregate.update(id, popupStore);
    }
    
    @Override
    public void deletePopupStore(Long id) {
        aggregate.delete(id);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        aggregate.incrementViewCount(id);
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        aggregate.incrementLikeCount(id);
    }
    
    @Override
    public void decrementLikeCount(Long id) {
        aggregate.decrementLikeCount(id);
    }
    
    @Override
    public void activatePopupStore(Long id) {
        aggregate.activate(id);
    }
    
    @Override
    public void deactivatePopupStore(Long id) {
        aggregate.deactivate(id);
    }

    /**
     * 팝업스토어를 검색합니다.
     */
    @Transactional(readOnly = true)
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
                .viewCount(popupStore.getViewCount())
                .likeCount(popupStore.getLikeCount())
                .createdAt(popupStore.getCreatedAt())
                .updatedAt(popupStore.getUpdatedAt())
                .build();
    }
} 