package com.sgyj.popupmoah.popupstore.infrastructure.web;

import com.sgyj.popupmoah.shared.response.ApiResponse;
import com.sgyj.popupmoah.popupstore.application.service.PopupStoreApplicationService;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팝업스토어 웹 컨트롤러 (Inbound Adapter)
 * 팝업스토어 관련 REST API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/popupstores")
@RequiredArgsConstructor
public class PopupStoreController {

    private final PopupStoreApplicationService popupStoreApplicationService;

    /**
     * 팝업스토어를 생성합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PopupStore>> createPopupStore(@RequestBody PopupStore popupStore) {
        log.info("팝업스토어 생성 요청: name={}", popupStore.getName());
        PopupStore created = popupStoreApplicationService.createPopupStore(popupStore);
        return ResponseEntity.ok(ApiResponse.success(created, "팝업스토어가 성공적으로 생성되었습니다."));
    }

    /**
     * ID로 팝업스토어를 조회합니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStore>> getPopupStoreById(@PathVariable Long id) {
        log.info("팝업스토어 조회 요청: id={}", id);
        PopupStore popupStore = popupStoreApplicationService.getPopupStoreById(id);
        return ResponseEntity.ok(ApiResponse.success(popupStore));
    }

    /**
     * 모든 활성화된 팝업스토어를 조회합니다.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PopupStore>>> getAllActivePopupStores() {
        log.info("활성화된 팝업스토어 목록 조회 요청");
        List<PopupStore> popupStores = popupStoreApplicationService.getAllActivePopupStores();
        return ResponseEntity.ok(ApiResponse.success(popupStores));
    }

    /**
     * 현재 진행 중인 팝업스토어를 조회합니다.
     */
    @GetMapping("/currently-active")
    public ResponseEntity<ApiResponse<List<PopupStore>>> getCurrentlyActivePopupStores() {
        log.info("현재 진행 중인 팝업스토어 목록 조회 요청");
        List<PopupStore> popupStores = popupStoreApplicationService.getCurrentlyActivePopupStores();
        return ResponseEntity.ok(ApiResponse.success(popupStores));
    }

    /**
     * 카테고리별 팝업스토어를 조회합니다.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<PopupStore>>> getPopupStoresByCategory(@PathVariable String category) {
        log.info("카테고리별 팝업스토어 조회 요청: category={}", category);
        List<PopupStore> popupStores = popupStoreApplicationService.getPopupStoresByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(popupStores));
    }

    /**
     * 위치별 팝업스토어를 조회합니다.
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<ApiResponse<List<PopupStore>>> getPopupStoresByLocation(@PathVariable String location) {
        log.info("위치별 팝업스토어 조회 요청: location={}", location);
        List<PopupStore> popupStores = popupStoreApplicationService.getPopupStoresByLocation(location);
        return ResponseEntity.ok(ApiResponse.success(popupStores));
    }

    /**
     * 이름으로 팝업스토어를 검색합니다.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PopupStore>>> searchPopupStoresByName(@RequestParam String name) {
        log.info("이름으로 팝업스토어 검색 요청: name={}", name);
        List<PopupStore> popupStores = popupStoreApplicationService.searchPopupStoresByName(name);
        return ResponseEntity.ok(ApiResponse.success(popupStores));
    }

    /**
     * 팝업스토어를 업데이트합니다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStore>> updatePopupStore(@PathVariable Long id, @RequestBody PopupStore popupStore) {
        log.info("팝업스토어 업데이트 요청: id={}", id);
        PopupStore updated = popupStoreApplicationService.updatePopupStore(id, popupStore);
        return ResponseEntity.ok(ApiResponse.success(updated, "팝업스토어가 성공적으로 업데이트되었습니다."));
    }

    /**
     * 팝업스토어를 삭제합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePopupStore(@PathVariable Long id) {
        log.info("팝업스토어 삭제 요청: id={}", id);
        popupStoreApplicationService.deletePopupStore(id);
        return ResponseEntity.ok(ApiResponse.success(null, "팝업스토어가 성공적으로 삭제되었습니다."));
    }

    /**
     * 팝업스토어 조회수를 증가시킵니다.
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long id) {
        log.info("팝업스토어 조회수 증가 요청: id={}", id);
        popupStoreApplicationService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "조회수가 증가되었습니다."));
    }

    /**
     * 팝업스토어 좋아요 수를 증가시킵니다.
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> incrementLikeCount(@PathVariable Long id) {
        log.info("팝업스토어 좋아요 수 증가 요청: id={}", id);
        popupStoreApplicationService.incrementLikeCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "좋아요가 증가되었습니다."));
    }

    /**
     * 팝업스토어 좋아요 수를 감소시킵니다.
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> decrementLikeCount(@PathVariable Long id) {
        log.info("팝업스토어 좋아요 수 감소 요청: id={}", id);
        popupStoreApplicationService.decrementLikeCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "좋아요가 감소되었습니다."));
    }
} 