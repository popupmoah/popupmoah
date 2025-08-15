package com.sgyj.popupmoah.adapter.web.category;

import com.sgyj.popupmoah.config.ApiResponse;
import com.sgyj.popupmoah.module.category.dto.CategoryRequest;
import com.sgyj.popupmoah.module.category.dto.CategoryResponse;
import com.sgyj.popupmoah.module.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 카테고리 REST API 컨트롤러
 * 카테고리 CRUD 및 계층 구조 관리 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 전체 카테고리 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        log.info("카테고리 목록 조회 요청");
        
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(ApiResponse.success(categories, "카테고리 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("카테고리 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("카테고리 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 루트 카테고리 목록 조회
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories() {
        log.info("루트 카테고리 목록 조회 요청");
        
        try {
            List<CategoryResponse> rootCategories = categoryService.getRootCategories();
            return ResponseEntity.ok(ApiResponse.success(rootCategories, "루트 카테고리 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("루트 카테고리 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("루트 카테고리 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 카테고리 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        log.info("카테고리 상세 조회 요청: id={}", id);
        
        try {
            CategoryResponse category = categoryService.getCategory(id);
            return ResponseEntity.ok(ApiResponse.success(category, "카테고리 정보를 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("카테고리 상세 조회 중 오류 발생: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("카테고리 정보 조회에 실패했습니다."));
        }
    }

    /**
     * 카테고리 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        
        log.info("카테고리 등록 요청: name={}, parentId={}", request.getName(), request.getParentId());
        
        try {
            CategoryResponse createdCategory = categoryService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdCategory, "카테고리가 성공적으로 등록되었습니다."));
        } catch (Exception e) {
            log.error("카테고리 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("카테고리 등록에 실패했습니다."));
        }
    }

    /**
     * 카테고리 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        
        log.info("카테고리 수정 요청: id={}, name={}", id, request.getName());
        
        try {
            CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(ApiResponse.success(updatedCategory, "카테고리가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("카테고리 수정 중 오류 발생: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("카테고리 수정에 실패했습니다."));
        }
    }

    /**
     * 카테고리 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("카테고리 삭제 요청: id={}", id);
        
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.success(null, "카테고리가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("카테고리 삭제 중 오류 발생: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("카테고리 삭제에 실패했습니다."));
        }
    }

    /**
     * 카테고리 순서 변경
     */
    @PutMapping("/{id}/reorder")
    public ResponseEntity<ApiResponse<CategoryResponse>> reorderCategory(
            @PathVariable Long id,
            @RequestParam Long newParentId,
            @RequestParam int newOrderIndex) {
        
        log.info("카테고리 순서 변경 요청: id={}, newParentId={}, newOrderIndex={}", id, newParentId, newOrderIndex);
        
        try {
            CategoryResponse reorderedCategory = categoryService.reorderCategory(id, newParentId, newOrderIndex);
            return ResponseEntity.ok(ApiResponse.success(reorderedCategory, "카테고리 순서가 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            log.error("카테고리 순서 변경 중 오류 발생: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("카테고리 순서 변경에 실패했습니다."));
        }
    }

    /**
     * 하위 카테고리 목록 조회
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getChildCategories(@PathVariable Long id) {
        log.info("하위 카테고리 목록 조회 요청: parentId={}", id);
        
        try {
            List<CategoryResponse> childCategories = categoryService.getChildCategories(id);
            return ResponseEntity.ok(ApiResponse.success(childCategories, "하위 카테고리 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("하위 카테고리 목록 조회 중 오류 발생: parentId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("하위 카테고리 목록 조회에 실패했습니다."));
        }
    }
}

