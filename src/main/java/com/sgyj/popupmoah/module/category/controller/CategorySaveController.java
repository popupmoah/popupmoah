package com.sgyj.popupmoah.module.category.controller;

import com.sgyj.popupmoah.module.category.controller.request.RegisterCategoryRequest;
import com.sgyj.popupmoah.module.category.controller.response.RegisteredCategory;
import com.sgyj.popupmoah.module.category.service.CategorySaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.sgyj.popupmoah.config.ApiResponse; // 표준 응답 DTO

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategorySaveController {

    private final CategorySaveService categoryService;

    /**
     * 카테고리를 등록하는 엔드포인트
     * 표준 응답 포맷 적용
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RegisteredCategory>> registerCategory(@RequestBody RegisterCategoryRequest request) {
        RegisteredCategory result = categoryService.saveCategory(request.mapToCommand());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 카테고리를 수정하는 엔드포인트
     * 표준 응답 포맷 적용
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<RegisteredCategory>> updateCategory(@PathVariable Long categoryId, @RequestBody RegisterCategoryRequest request) {
        RegisteredCategory updated = categoryService.updateCategory(categoryId, request.mapToCommand());
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * 카테고리를 삭제하는 엔드포인트
     * 표준 응답 포맷 적용
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(null, "삭제 성공"));
    }
    
}
