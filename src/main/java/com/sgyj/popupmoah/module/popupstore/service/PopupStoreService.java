package com.sgyj.popupmoah.module.popupstore.service;

import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 팝업스토어 서비스
 * 팝업스토어 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupStoreService {

    /**
     * 팝업스토어 목록 조회 (페이지네이션 지원)
     */
    public Page<PopupStoreResponse> getPopupStores(String search, Long categoryId, String startDate, String endDate, Pageable pageable) {
        log.info("팝업스토어 목록 조회: search={}, categoryId={}, startDate={}, endDate={}", search, categoryId, startDate, endDate);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        // 현재는 목 데이터 반환
        return Page.empty(pageable);
    }

    /**
     * 팝업스토어 상세 조회
     */
    public PopupStoreResponse getPopupStore(Long id) {
        log.info("팝업스토어 상세 조회: id={}", id);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        return PopupStoreResponse.builder()
                .id(id)
                .name("테스트 팝업스토어")
                .description("테스트 설명")
                .address("서울시 강남구")
                .category("패션")
                .isActive(true)
                .build();
    }

    /**
     * 팝업스토어 등록
     */
    @Transactional
    public PopupStoreResponse createPopupStore(PopupStoreRequest request) {
        log.info("팝업스토어 등록: name={}", request.getName());
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 저장
        return PopupStoreResponse.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .category(request.getCategory())
                .isActive(request.getIsActive())
                .build();
    }

    /**
     * 팝업스토어 수정
     */
    @Transactional
    public PopupStoreResponse updatePopupStore(Long id, PopupStoreRequest request) {
        log.info("팝업스토어 수정: id={}, name={}", id, request.getName());
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 수정
        return PopupStoreResponse.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .category(request.getCategory())
                .isActive(request.getIsActive())
                .build();
    }

    /**
     * 팝업스토어 삭제
     */
    @Transactional
    public void deletePopupStore(Long id) {
        log.info("팝업스토어 삭제: id={}", id);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 삭제
    }

    /**
     * 카테고리별 팝업스토어 목록 조회
     */
    public List<PopupStoreResponse> getPopupStoresByCategory(Long categoryId) {
        log.info("카테고리별 팝업스토어 목록 조회: categoryId={}", categoryId);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        return List.of();
    }

    /**
     * 인기 팝업스토어 목록 조회
     */
    public List<PopupStoreResponse> getPopularPopupStores(int limit) {
        log.info("인기 팝업스토어 목록 조회: limit={}", limit);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        return List.of();
    }
}
