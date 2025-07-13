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

    /**
     * 카테고리를 수정하는 메서드
     *
     * @param categoryId 수정할 카테고리 ID
     * @param command 수정할 정보가 담긴 명령 객체
     * @return 수정된 카테고리 정보
     */
    public RegisteredCategory updateCategory(Long categoryId, CategorySaveCommand command) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. id=" + categoryId));
        category.update(command.getName(), command.getDescription(), command.getSortOrder(), command.isActive());
        Category updatedCategory = categoryRepository.save(category);
        return RegisteredCategory.of(updatedCategory.getId(), updatedCategory.getName(), updatedCategory.getDescription(), updatedCategory.getSortOrder(), updatedCategory.isActive());
    }

    /**
     * 카테고리를 삭제하는 메서드
     *
     * @param categoryId 삭제할 카테고리 ID
     */
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. id=" + categoryId));
        categoryRepository.delete(category);
    }
}
