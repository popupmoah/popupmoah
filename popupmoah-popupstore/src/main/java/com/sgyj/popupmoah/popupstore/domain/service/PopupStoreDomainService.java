package com.sgyj.popupmoah.popupstore.domain.service;

import com.sgyj.popupmoah.shared.exception.ResourceNotFoundException;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.port.PopupStoreRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 팝업스토어 도메인 서비스
 * 팝업스토어 관련 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PopupStoreDomainService {

    private final PopupStoreRepositoryPort popupStoreRepositoryPort;

    /**
     * 팝업스토어를 생성합니다.
     */
    public PopupStore createPopupStore(PopupStore popupStore) {
        log.info("팝업스토어 생성: name={}", popupStore.getName());
        return popupStoreRepositoryPort.save(popupStore);
    }

    /**
     * ID로 팝업스토어를 조회합니다.
     */
    public PopupStore getPopupStoreById(Long id) {
        log.info("팝업스토어 조회: id={}", id);
        return popupStoreRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PopupStore", id));
    }

    /**
     * 모든 활성화된 팝업스토어를 조회합니다.
     */
    public List<PopupStore> getAllActivePopupStores() {
        log.info("활성화된 팝업스토어 목록 조회");
        return popupStoreRepositoryPort.findAllActive();
    }

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    public List<PopupStore> getCurrentlyActivePopupStores() {
        log.info("현재 진행 중인 팝업스토어 목록 조회");
        return popupStoreRepositoryPort.findCurrentlyActive();
    }

    /**
     * 카테고리별 팝업스토어를 조회합니다.
     */
    public List<PopupStore> getPopupStoresByCategory(String category) {
        log.info("카테고리별 팝업스토어 조회: category={}", category);
        return popupStoreRepositoryPort.findByCategory(category);
    }

    /**
     * 위치별 팝업스토어를 조회합니다.
     */
    public List<PopupStore> getPopupStoresByLocation(String location) {
        log.info("위치별 팝업스토어 조회: location={}", location);
        return popupStoreRepositoryPort.findByLocation(location);
    }

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    public List<PopupStore> searchPopupStoresByName(String name) {
        log.info("이름으로 팝업스토어 검색: name={}", name);
        return popupStoreRepositoryPort.findByNameContaining(name);
    }

    /**
     * 팝업스토어를 업데이트합니다.
     */
    public PopupStore updatePopupStore(Long id, PopupStore popupStore) {
        log.info("팝업스토어 업데이트: id={}", id);
        PopupStore existingPopupStore = getPopupStoreById(id);
        
        // 기존 데이터 유지하면서 업데이트
        existingPopupStore.setName(popupStore.getName());
        existingPopupStore.setDescription(popupStore.getDescription());
        existingPopupStore.setImageUrl(popupStore.getImageUrl());
        existingPopupStore.setSourceUrl(popupStore.getSourceUrl());
        existingPopupStore.setCategory(popupStore.getCategory());
        existingPopupStore.setStartDate(popupStore.getStartDate());
        existingPopupStore.setEndDate(popupStore.getEndDate());
        existingPopupStore.setLocation(popupStore.getLocation());
        existingPopupStore.setActive(popupStore.getActive());
        
        return popupStoreRepositoryPort.save(existingPopupStore);
    }

    /**
     * 팝업스토어를 삭제합니다.
     */
    public void deletePopupStore(Long id) {
        log.info("팝업스토어 삭제: id={}", id);
        getPopupStoreById(id); // 존재 여부 확인
        popupStoreRepositoryPort.deleteById(id);
    }

    /**
     * 팝업스토어 조회수를 증가시킵니다.
     */
    public void incrementViewCount(Long id) {
        log.info("팝업스토어 조회수 증가: id={}", id);
        PopupStore popupStore = getPopupStoreById(id);
        popupStore.incrementViewCount();
        popupStoreRepositoryPort.save(popupStore);
    }

    /**
     * 팝업스토어 좋아요 수를 증가시킵니다.
     */
    public void incrementLikeCount(Long id) {
        log.info("팝업스토어 좋아요 수 증가: id={}", id);
        PopupStore popupStore = getPopupStoreById(id);
        popupStore.incrementLikeCount();
        popupStoreRepositoryPort.save(popupStore);
    }

    /**
     * 팝업스토어 좋아요 수를 감소시킵니다.
     */
    public void decrementLikeCount(Long id) {
        log.info("팝업스토어 좋아요 수 감소: id={}", id);
        PopupStore popupStore = getPopupStoreById(id);
        popupStore.decrementLikeCount();
        popupStoreRepositoryPort.save(popupStore);
    }
} 