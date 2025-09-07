package com.sgyj.popupmoah.domain.category.infrastructure.web;

import com.sgyj.popupmoah.domain.category.application.dto.CategoryCreateRequest;
import com.sgyj.popupmoah.domain.category.application.dto.CategoryResponse;
import com.sgyj.popupmoah.domain.category.application.dto.CategoryUpdateRequest;
import com.sgyj.popupmoah.domain.category.entity.Category;
import com.sgyj.popupmoah.domain.category.port.CategoryServicePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 카테고리 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServicePort categoryServicePort;

    /**
     * 카테고리 생성
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        log.info("카테고리 생성 API 호출: name={}, parentId={}", request.getName(), request.getParentId());

        try {
            Category category = Category.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();

            // 부모 카테고리 설정
            if (request.getParentId() != null) {
                Category parent = categoryServicePort.getCategoryById(request.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + request.getParentId()));
                category.setParent(parent);
            }

            Category savedCategory = categoryServicePort.createCategory(category);
            CategoryResponse response = convertToResponse(savedCategory);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 생성 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 카테고리 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        log.info("카테고리 수정 API 호출: id={}, name={}", id, request.getName());

        try {
            Category category = Category.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();

            // 부모 카테고리 설정
            if (request.getParentId() != null) {
                Category parent = categoryServicePort.getCategoryById(request.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다: " + request.getParentId()));
                category.setParent(parent);
            }

            Category updatedCategory = categoryServicePort.updateCategory(id, category);
            CategoryResponse response = convertToResponse(updatedCategory);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 수정 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 카테고리 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id) {
        log.info("카테고리 삭제 API 호출: id={}", id);

        try {
            categoryServicePort.deleteCategory(id);
            return ResponseEntity.ok(Map.of(
                "message", "카테고리가 성공적으로 삭제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 삭제 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 카테고리 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        log.info("카테고리 상세 조회 API 호출: id={}", id);

        try {
            Category category = categoryServicePort.getCategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));

            CategoryResponse response = convertToResponse(category);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 상세 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 전체 카테고리 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("전체 카테고리 목록 조회 API 호출");

        try {
            List<Category> categories = categoryServicePort.getAllCategories();
            List<CategoryResponse> responses = categories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("전체 카테고리 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 최상위 카테고리 목록 조회
     */
    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        log.info("최상위 카테고리 목록 조회 API 호출");

        try {
            List<Category> categories = categoryServicePort.getRootCategories();
            List<CategoryResponse> responses = categories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("최상위 카테고리 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 활성화된 최상위 카테고리 목록 조회
     */
    @GetMapping("/root/active")
    public ResponseEntity<List<CategoryResponse>> getActiveRootCategories() {
        log.info("활성화된 최상위 카테고리 목록 조회 API 호출");

        try {
            List<Category> categories = categoryServicePort.getActiveRootCategories();
            List<CategoryResponse> responses = categories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("활성화된 최상위 카테고리 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 자식 카테고리 목록 조회
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CategoryResponse>> getChildrenCategories(@PathVariable Long parentId) {
        log.info("자식 카테고리 목록 조회 API 호출: parentId={}", parentId);

        try {
            List<Category> categories = categoryServicePort.getChildrenByParentId(parentId);
            List<CategoryResponse> responses = categories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("자식 카테고리 목록 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 카테고리 트리 구조 조회
     */
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        log.info("카테고리 트리 구조 조회 API 호출");

        try {
            List<Category> categories = categoryServicePort.getCategoryTree();
            List<CategoryResponse> responses = categories.stream()
                    .map(this::convertToResponseWithChildren)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("카테고리 트리 구조 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 카테고리명 중복 확인
     */
    @GetMapping("/check-name")
    public ResponseEntity<Object> checkNameExists(@RequestParam String name) {
        log.info("카테고리명 중복 확인 API 호출: name={}", name);

        try {
            boolean exists = categoryServicePort.existsByName(name);
            return ResponseEntity.ok(Map.of(
                "name", name,
                "exists", exists
            ));
        } catch (Exception e) {
            log.error("카테고리명 중복 확인 실패: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Category 엔티티를 CategoryResponse로 변환
     */
    private CategoryResponse convertToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .children(List.of())
                .childCount(0)
                .level(calculateLevel(category))
                .build();
    }

    /**
     * Category 엔티티를 CategoryResponse로 변환 (자식 포함)
     */
    private CategoryResponse convertToResponseWithChildren(Category category) {
        CategoryResponse response = convertToResponse(category);
        
        // 자식 카테고리 조회
        List<Category> children = categoryServicePort.getChildrenByParentId(category.getId());
        List<CategoryResponse> childResponses = children.stream()
                .map(this::convertToResponseWithChildren)
                .collect(Collectors.toList());
        
        response.setChildren(childResponses);
        response.setChildCount(childResponses.size());
        
        return response;
    }

    /**
     * 카테고리 레벨 계산
     */
    private int calculateLevel(Category category) {
        int level = 0;
        Category current = category;
        
        while (current.getParent() != null) {
            level++;
            current = current.getParent();
        }
        
        return level;
    }
}



