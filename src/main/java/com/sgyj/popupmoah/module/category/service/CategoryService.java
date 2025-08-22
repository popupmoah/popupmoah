package com.sgyj.popupmoah.module.category.service;

import com.sgyj.popupmoah.module.category.dto.CategoryRequest;
import com.sgyj.popupmoah.module.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 카테고리 서비스
 * 카테고리 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    /**
     * 전체 카테고리 목록 조회
     */
    public List<CategoryResponse> getAllCategories() {
        log.info("전체 카테고리 목록 조회");
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        return List.of(
            CategoryResponse.builder()
                .id(1L)
                .name("패션")
                .description("패션 관련 팝업스토어")
                .level(0)
                .orderIndex(1)
                .isActive(true)
                .children(List.of(
                    CategoryResponse.builder()
                        .id(2L)
                        .name("의류")
                        .description("의류 관련 팝업스토어")
                        .parentId(1L)
                        .level(1)
                        .orderIndex(1)
                        .isActive(true)
                        .build(),
                    CategoryResponse.builder()
                        .id(3L)
                        .name("신발")
                        .description("신발 관련 팝업스토어")
                        .parentId(1L)
                        .level(1)
                        .orderIndex(2)
                        .isActive(true)
                        .build()
                ))
                .build(),
            CategoryResponse.builder()
                .id(4L)
                .name("푸드")
                .description("푸드 관련 팝업스토어")
                .level(0)
                .orderIndex(2)
                .isActive(true)
                .build()
        );
    }

    /**
     * 루트 카테고리 목록 조회
     */
    public List<CategoryResponse> getRootCategories() {
        log.info("루트 카테고리 목록 조회");
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        return List.of(
            CategoryResponse.builder()
                .id(1L)
                .name("패션")
                .description("패션 관련 팝업스토어")
                .level(0)
                .orderIndex(1)
                .isActive(true)
                .build(),
            CategoryResponse.builder()
                .id(4L)
                .name("푸드")
                .description("푸드 관련 팝업스토어")
                .level(0)
                .orderIndex(2)
                .isActive(true)
                .build()
        );
    }

    /**
     * 카테고리 상세 조회
     */
    public CategoryResponse getCategory(Long id) {
        log.info("카테고리 상세 조회: id={}", id);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        return CategoryResponse.builder()
                .id(id)
                .name("테스트 카테고리")
                .description("테스트 설명")
                .level(0)
                .orderIndex(1)
                .isActive(true)
                .build();
    }

    /**
     * 카테고리 등록
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("카테고리 등록: name={}, parentId={}", request.getName(), request.getParentId());
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 저장
        return CategoryResponse.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .level(0)
                .orderIndex(request.getOrderIndex())
                .isActive(request.getIsActive())
                .build();
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("카테고리 수정: id={}, name={}", id, request.getName());
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 수정
        return CategoryResponse.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .level(0)
                .orderIndex(request.getOrderIndex())
                .isActive(request.getIsActive())
                .build();
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long id) {
        log.info("카테고리 삭제: id={}", id);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 삭제
    }

    /**
     * 카테고리 순서 변경
     */
    @Transactional
    public CategoryResponse reorderCategory(Long id, Long newParentId, int newOrderIndex) {
        log.info("카테고리 순서 변경: id={}, newParentId={}, newOrderIndex={}", id, newParentId, newOrderIndex);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 수정
        return CategoryResponse.builder()
                .id(id)
                .name("테스트 카테고리")
                .description("테스트 설명")
                .parentId(newParentId)
                .level(0)
                .orderIndex(newOrderIndex)
                .isActive(true)
                .build();
    }

    /**
     * 하위 카테고리 목록 조회
     */
    public List<CategoryResponse> getChildCategories(Long parentId) {
        log.info("하위 카테고리 목록 조회: parentId={}", parentId);
        
        // TODO: 실제 구현에서는 Repository를 통해 데이터를 조회
        return List.of(
            CategoryResponse.builder()
                .id(2L)
                .name("의류")
                .description("의류 관련 팝업스토어")
                .parentId(parentId)
                .level(1)
                .orderIndex(1)
                .isActive(true)
                .build(),
            CategoryResponse.builder()
                .id(3L)
                .name("신발")
                .description("신발 관련 팝업스토어")
                .parentId(parentId)
                .level(1)
                .orderIndex(2)
                .isActive(true)
                .build()
        );
    }
}

