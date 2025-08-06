package com.sgyj.popupmoah.popupstore.application.service;

import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import com.sgyj.popupmoah.popupstore.domain.service.PopupStoreDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 팝업스토어 애플리케이션 서비스
 * 팝업스토어 관련 유스케이스를 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PopupStoreApplicationService {

    private final PopupStoreDomainService popupStoreDomainService;

    /**
     * 팝업스토어를 생성합니다.
     */
    public PopupStore createPopupStore(PopupStore popupStore) {
        log.info("팝업스토어 생성 요청: name={}", popupStore.getName());
        return popupStoreDomainService.createPopupStore(popupStore);
    }

    /**
     * ID로 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public PopupStore getPopupStoreById(Long id) {
        log.info("팝업스토어 조회 요청: id={}", id);
        return popupStoreDomainService.getPopupStoreById(id);
    }

    /**
     * 모든 활성화된 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> getAllActivePopupStores() {
        log.info("활성화된 팝업스토어 목록 조회 요청");
        return popupStoreDomainService.getAllActivePopupStores();
    }

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> getCurrentlyActivePopupStores() {
        log.info("현재 진행 중인 팝업스토어 목록 조회 요청");
        return popupStoreDomainService.getCurrentlyActivePopupStores();
    }

    /**
     * 카테고리별 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> getPopupStoresByCategory(String category) {
        log.info("카테고리별 팝업스토어 조회 요청: category={}", category);
        return popupStoreDomainService.getPopupStoresByCategory(category);
    }

    /**
     * 위치별 팝업스토어를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> getPopupStoresByLocation(String location) {
        log.info("위치별 팝업스토어 조회 요청: location={}", location);
        return popupStoreDomainService.getPopupStoresByLocation(location);
    }

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    @Transactional(readOnly = true)
    public List<PopupStore> searchPopupStoresByName(String name) {
        log.info("이름으로 팝업스토어 검색 요청: name={}", name);
        return popupStoreDomainService.searchPopupStoresByName(name);
    }

    /**
     * 팝업스토어를 업데이트합니다.
     */
    public PopupStore updatePopupStore(Long id, PopupStore popupStore) {
        log.info("팝업스토어 업데이트 요청: id={}", id);
        return popupStoreDomainService.updatePopupStore(id, popupStore);
    }

    /**
     * 팝업스토어를 삭제합니다.
     */
    public void deletePopupStore(Long id) {
        log.info("팝업스토어 삭제 요청: id={}", id);
        popupStoreDomainService.deletePopupStore(id);
    }

    /**
     * 팝업스토어 조회수를 증가시킵니다.
     */
    public void incrementViewCount(Long id) {
        log.info("팝업스토어 조회수 증가 요청: id={}", id);
        popupStoreDomainService.incrementViewCount(id);
    }

    /**
     * 팝업스토어 좋아요 수를 증가시킵니다.
     */
    public void incrementLikeCount(Long id) {
        log.info("팝업스토어 좋아요 수 증가 요청: id={}", id);
        popupStoreDomainService.incrementLikeCount(id);
    }

    /**
     * 팝업스토어 좋아요 수를 감소시킵니다.
     */
    public void decrementLikeCount(Long id) {
        log.info("팝업스토어 좋아요 수 감소 요청: id={}", id);
        popupStoreDomainService.decrementLikeCount(id);
    }
} 