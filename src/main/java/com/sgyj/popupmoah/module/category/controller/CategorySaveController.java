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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategorySaveController {

    private final CategorySaveService categoryService;

    /**
     * 카테고리를 등록하는 엔드포인트
     *
     * @param request 등록할 카테고리 정보
     * @return 등록된 카테고리 정보
     */
    @PostMapping
    public ResponseEntity<RegisteredCategory> registerCategory(@RequestBody RegisterCategoryRequest request) {
        return ResponseEntity.ok(categoryService.saveCategory(request.mapToCommand()));
    }

    /**
     * 카테고리를 수정하는 엔드포인트
     *
     * @param categoryId 수정할 카테고리 ID
     * @param request 수정할 카테고리 정보
     * @return 수정된 카테고리 정보
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<RegisteredCategory> updateCategory(@PathVariable Long categoryId, @RequestBody RegisterCategoryRequest request) {
        RegisteredCategory updated = categoryService.updateCategory(categoryId, request.mapToCommand());
        return ResponseEntity.ok(updated);
    }

    /**
     * 카테고리를 삭제하는 엔드포인트
     *
     * @param categoryId 삭제할 카테고리 ID
     * @return 성공 시 204 No Content 반환
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
    
}
