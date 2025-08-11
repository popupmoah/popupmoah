package com.sgyj.popupmoah.popupstore.infrastructure.web;

import com.sgyj.popupmoah.common.response.ApiResponse;
import com.sgyj.popupmoah.popupstore.application.service.PopupStoreApplicationService;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팝업스토어 웹 컨트롤러
 * HTTP 요청을 처리하는 인프라스트럭처 어댑터
 */
@RestController
@RequestMapping("/api/popupstores")
@RequiredArgsConstructor
public class PopupStoreController {
    
    private final PopupStoreApplicationService applicationService;
    
    /**
     * 팝업스토어를 생성합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PopupStore>> createPopupStore(@RequestBody PopupStore popupStore) {
        PopupStore created = applicationService.createPopupStore(popupStore);
        return ResponseEntity.ok(ApiResponse.success(created));
    }
    
    /**
     * ID로 팝업스토어를 조회합니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStore>> getPopupStore(@PathVariable Long id) {
        return applicationService.getPopupStore(id)
                .map(popupStore -> ResponseEntity.ok(ApiResponse.success(popupStore)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 모든 팝업스토어를 조회합니다.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PopupStore>>> getAllPopupStores() {
        List<PopupStore> popupStores = applicationService.getAllPopupStores();
        return ResponseEntity.ok(ApiResponse.success(popupStores));
    }
    
    /**
     * 활성화된 팝업스토어만 조회합니다.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PopupStore>>> getActivePopupStores() {
        List<PopupStore> popupStores = applicationService.getActivePopupStores();
        return ResponseEntity.ok(ApiResponse.success(popupStores));
    }
    
    /**
     * 팝업스토어를 업데이트합니다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStore>> updatePopupStore(@PathVariable Long id, @RequestBody PopupStore popupStore) {
        PopupStore updated = applicationService.updatePopupStore(id, popupStore);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
    
    /**
     * 팝업스토어를 삭제합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePopupStore(@PathVariable Long id) {
        applicationService.deletePopupStore(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 팝업스토어 조회수를 증가시킵니다.
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long id) {
        applicationService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 팝업스토어 좋아요 수를 증가시킵니다.
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> incrementLikeCount(@PathVariable Long id) {
        applicationService.incrementLikeCount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 팝업스토어 좋아요 수를 감소시킵니다.
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> decrementLikeCount(@PathVariable Long id) {
        applicationService.decrementLikeCount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 팝업스토어를 활성화합니다.
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activatePopupStore(@PathVariable Long id) {
        applicationService.activatePopupStore(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 팝업스토어를 비활성화합니다.
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivatePopupStore(@PathVariable Long id) {
        applicationService.deactivatePopupStore(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 