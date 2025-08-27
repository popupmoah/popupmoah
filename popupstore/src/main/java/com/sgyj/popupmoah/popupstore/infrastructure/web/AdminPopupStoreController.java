package com.sgyj.popupmoah.popupstore.infrastructure.web;

import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreResponse;
import com.sgyj.popupmoah.popupstore.application.service.PopupStoreApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 관리자 팝업스토어 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/popupstores")
@RequiredArgsConstructor
public class AdminPopupStoreController {

    private final PopupStoreApplicationService popupStoreService;

    /**
     * 승인 대기 중인 팝업스토어 목록 조회
     */
    @GetMapping("/pending")
    public ResponseEntity<Page<PopupStoreResponse>> getPendingPopupStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        log.info("승인 대기 팝업스토어 목록 조회: page={}, size={}, sortBy={}, sortDirection={}", 
                page, size, sortBy, sortDirection);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PopupStoreResponse> pendingStores = popupStoreService.getPendingPopupStores(pageable);
        return ResponseEntity.ok(pendingStores);
    }

    /**
     * 팝업스토어 승인
     */
    @PostMapping("/{popupStoreId}/approve")
    public ResponseEntity<Map<String, String>> approvePopupStore(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 승인 요청: popupStoreId={}", popupStoreId);
        
        popupStoreService.approvePopupStore(popupStoreId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "팝업스토어가 성공적으로 승인되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 팝업스토어 거부
     */
    @PostMapping("/{popupStoreId}/reject")
    public ResponseEntity<Map<String, String>> rejectPopupStore(
            @PathVariable Long popupStoreId,
            @RequestParam(required = false) String reason) {
        log.info("팝업스토어 거부 요청: popupStoreId={}, reason={}", popupStoreId, reason);
        
        popupStoreService.rejectPopupStore(popupStoreId, reason);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "팝업스토어가 성공적으로 거부되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 팝업스토어 비활성화
     */
    @PostMapping("/{popupStoreId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivatePopupStore(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 비활성화 요청: popupStoreId={}", popupStoreId);
        
        popupStoreService.deactivatePopupStore(popupStoreId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "팝업스토어가 성공적으로 비활성화되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 팝업스토어 재활성화
     */
    @PostMapping("/{popupStoreId}/activate")
    public ResponseEntity<Map<String, String>> activatePopupStore(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 재활성화 요청: popupStoreId={}", popupStoreId);
        
        popupStoreService.activatePopupStore(popupStoreId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "팝업스토어가 성공적으로 재활성화되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 팝업스토어 목록 조회 (관리자용)
     */
    @GetMapping
    public ResponseEntity<Page<PopupStoreResponse>> getAllPopupStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String status) {
        log.info("전체 팝업스토어 목록 조회: page={}, size={}, sortBy={}, sortDirection={}, status={}", 
                page, size, sortBy, sortDirection, status);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PopupStoreResponse> popupStores = popupStoreService.getAllPopupStoresForAdmin(pageable, status);
        return ResponseEntity.ok(popupStores);
    }

    /**
     * 팝업스토어 상세 정보 조회 (관리자용)
     */
    @GetMapping("/{popupStoreId}")
    public ResponseEntity<PopupStoreResponse> getPopupStoreForAdmin(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 상세 정보 조회 (관리자용): popupStoreId={}", popupStoreId);
        
        PopupStoreResponse popupStore = popupStoreService.getPopupStoreById(popupStoreId);
        return ResponseEntity.ok(popupStore);
    }

    /**
     * 팝업스토어 삭제 (관리자용)
     */
    @DeleteMapping("/{popupStoreId}")
    public ResponseEntity<Map<String, String>> deletePopupStore(@PathVariable Long popupStoreId) {
        log.info("팝업스토어 삭제 요청 (관리자용): popupStoreId={}", popupStoreId);
        
        popupStoreService.deletePopupStore(popupStoreId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "팝업스토어가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}
