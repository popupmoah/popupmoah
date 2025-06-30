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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategorySaveController {

    private final CategorySaveService categoryService;

    @PostMapping
    public ResponseEntity<RegisteredCategory> registerCategory(@RequestBody RegisterCategoryRequest request) {
        return ResponseEntity.ok(categoryService.saveCategory(request.mapToCommand()));
    }
}
