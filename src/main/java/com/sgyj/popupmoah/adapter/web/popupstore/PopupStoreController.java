package com.sgyj.popupmoah.adapter.web.popupstore;

import com.sgyj.popupmoah.config.ApiResponse;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreRequest;
import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreResponse;
import com.sgyj.popupmoah.module.popupstore.service.PopupStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 팝업스토어 REST API 컨트롤러
 * 팝업스토어 CRUD 및 검색 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/popupstores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PopupStoreController {

    private final PopupStoreService popupStoreService;

    /**
     * 팝업스토어 목록 조회 (페이지네이션 지원)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PopupStoreResponse>>> getPopupStores(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        
        log.info("팝업스토어 목록 조회 요청: search={}, categoryId={}, startDate={}, endDate={}, page={}, size={}", 
                search, categoryId, startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<PopupStoreResponse> popupStores = popupStoreService.getPopupStores(search, categoryId, startDate, endDate, pageable);
            return ResponseEntity.ok(ApiResponse.success(popupStores, "팝업스토어 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("팝업스토어 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("팝업스토어 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 팝업스토어 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStoreResponse>> getPopupStore(@PathVariable Long id) {
        log.info("팝업스토어 상세 조회 요청: id={}", id);
        
        try {
            PopupStoreResponse popupStore = popupStoreService.getPopupStore(id);
            return ResponseEntity.ok(ApiResponse.success(popupStore, "팝업스토어 정보를 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("팝업스토어 상세 조회 중 오류 발생: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("팝업스토어 정보 조회에 실패했습니다."));
        }
    }

    /**
     * 팝업스토어 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PopupStoreResponse>> createPopupStore(
            @Valid @RequestBody PopupStoreRequest request) {
        
        log.info("팝업스토어 등록 요청: name={}", request.getName());
        
        try {
            PopupStoreResponse createdPopupStore = popupStoreService.createPopupStore(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdPopupStore, "팝업스토어가 성공적으로 등록되었습니다."));
        } catch (Exception e) {
            log.error("팝업스토어 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("팝업스토어 등록에 실패했습니다."));
        }
    }

    /**
     * 팝업스토어 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStoreResponse>> updatePopupStore(
            @PathVariable Long id,
            @Valid @RequestBody PopupStoreRequest request) {
        
        log.info("팝업스토어 수정 요청: id={}, name={}", id, request.getName());
        
        try {
            PopupStoreResponse updatedPopupStore = popupStoreService.updatePopupStore(id, request);
            return ResponseEntity.ok(ApiResponse.success(updatedPopupStore, "팝업스토어가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("팝업스토어 수정 중 오류 발생: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("팝업스토어 수정에 실패했습니다."));
        }
    }

    /**
     * 팝업스토어 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePopupStore(@PathVariable Long id) {
        log.info("팝업스토어 삭제 요청: id={}", id);
        
        try {
            popupStoreService.deletePopupStore(id);
            return ResponseEntity.ok(ApiResponse.success(null, "팝업스토어가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("팝업스토어 삭제 중 오류 발생: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("팝업스토어 삭제에 실패했습니다."));
        }
    }

    /**
     * 카테고리별 팝업스토어 목록 조회
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<PopupStoreResponse>>> getPopupStoresByCategory(
            @PathVariable Long categoryId) {
        
        log.info("카테고리별 팝업스토어 목록 조회 요청: categoryId={}", categoryId);
        
        try {
            List<PopupStoreResponse> popupStores = popupStoreService.getPopupStoresByCategory(categoryId);
            return ResponseEntity.ok(ApiResponse.success(popupStores, "카테고리별 팝업스토어 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("카테고리별 팝업스토어 목록 조회 중 오류 발생: categoryId={}", categoryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("카테고리별 팝업스토어 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 인기 팝업스토어 목록 조회
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopupStoreResponse>>> getPopularPopupStores(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("인기 팝업스토어 목록 조회 요청: limit={}", limit);
        
        try {
            List<PopupStoreResponse> popularPopupStores = popupStoreService.getPopularPopupStores(limit);
            return ResponseEntity.ok(ApiResponse.success(popularPopupStores, "인기 팝업스토어 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("인기 팝업스토어 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("인기 팝업스토어 목록 조회에 실패했습니다."));
        }
    }
}

