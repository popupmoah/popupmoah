package com.sgyj.popupmoah.module.category.service;

import com.sgyj.popupmoah.module.category.repository.CategoryRepository;
import com.sgyj.popupmoah.module.category.controller.response.RegisteredCategory;
import com.sgyj.popupmoah.module.category.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategorySaveService {

    private final CategoryRepository categoryRepository;

    public CategorySaveService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 카테고리를 저장하는 메서드
     *
     * @param command 카테고리 저장 명령 객체
     * @return 저장된 카테고리 정보
     */
    public RegisteredCategory saveCategory(CategorySaveCommand command) {
        Category category = Category.of(command.getName(), command.getDescription(), command.getSortOrder(), command.isActive());
        Category savedCategory = categoryRepository.save(category);
        return RegisteredCategory.of(savedCategory.getId(), savedCategory.getName(), savedCategory.getDescription(), savedCategory.getSortOrder(), savedCategory.isActive());
    }
}
