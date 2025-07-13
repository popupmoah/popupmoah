package com.sgyj.popupmoah.module.category.service;

import com.sgyj.popupmoah.module.category.controller.response.RegisteredCategory;
import com.sgyj.popupmoah.module.category.entity.Category;
import com.sgyj.popupmoah.module.category.repository.CategoryRepository;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.assertj.core.api.Assertions;

@ExtendWith(MockitoExtension.class)
class CategorySaveServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Validator validator;
    
    private CategorySaveService categorySaveService;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        categorySaveService = new CategorySaveService(categoryRepository, validator);
    }

    @Test
    @DisplayName("카테고리 등록 성공")
    void saveCategory_success() {
        // given
        CategorySaveCommand command = CategorySaveCommand.builder()
                .name("테스트카테고리")
                .description("설명")
                .sortOrder(1.0f)
                .active(true)
                .build();
        Category saved = Category.of("테스트카테고리", "설명", 1.0f, true);
        // id 세팅 (가짜)
        Field idField;
        try {
            idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(saved, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        // when
        RegisteredCategory result = categorySaveService.saveCategory(command);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트카테고리");
        assertThat(result.getDescription()).isEqualTo("설명");
        assertThat(result.getSortOrder()).isEqualTo(1.0f);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("카테고리 등록 - name이 null이면 예외 발생")
    void saveCategory_nameNull_fail() {
        // given
        CategorySaveCommand command = CategorySaveCommand.builder()
                .name(null)
                .description("설명")
                .sortOrder(1.0f)
                .active(true)
                .build();

        // when & then
        Assertions.assertThatThrownBy(() ->
                categorySaveService.saveCategory(command)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("카테고리 이름은 필수입니다.");

        // save가 호출되지 않았는지 검증
        verify(categoryRepository, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any(Category.class));
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategory_success() {
        // given
        Long categoryId = 1L;
        Category origin = Category.of("원본", "설명", 1.0f, true);
        // id 세팅
        try {
            Field idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(origin, categoryId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CategorySaveCommand command = CategorySaveCommand.builder()
                .name("수정된카테고리")
                .description("수정설명")
                .sortOrder(2.0f)
                .active(false)
                .build();
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(origin));
        when(categoryRepository.save(any(Category.class))).thenReturn(origin);

        // when
        RegisteredCategory result = categorySaveService.updateCategory(categoryId, command);

        // then
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo("수정된카테고리");
        assertThat(result.getDescription()).isEqualTo("수정설명");
        assertThat(result.getSortOrder()).isEqualTo(2.0f);
        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("카테고리 수정 - 존재하지 않는 ID 예외")
    void updateCategory_notFound() {
        // given
        Long notExistId = 999L;
        CategorySaveCommand command = CategorySaveCommand.builder()
                .name("수정")
                .description("수정")
                .sortOrder(1.0f)
                .active(true)
                .build();
        when(categoryRepository.findById(notExistId)).thenReturn(java.util.Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() ->
                categorySaveService.updateCategory(notExistId, command)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("존재하지 않습니다");
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_success() {
        // given
        Long categoryId = 1L;
        Category category = Category.of("카테고리", "설명", 1.0f, true);
        try {
            Field idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, categoryId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));

        // when & then (예외 발생 안 하면 성공)
        categorySaveService.deleteCategory(categoryId);
    }

    @Test
    @DisplayName("카테고리 삭제 - 존재하지 않는 ID 예외")
    void deleteCategory_notFound() {
        // given
        Long notExistId = 999L;
        when(categoryRepository.findById(notExistId)).thenReturn(java.util.Optional.empty());

        // when & then
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                categorySaveService.deleteCategory(notExistId)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("존재하지 않습니다");
    }
} 