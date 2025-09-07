package com.sgyj.popupmoah.popupstore.infrastructure.web;

import com.sgyj.popupmoah.common.response.ApiResponse;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreCreateRequest;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreResponse;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreUpdateRequest;
import com.sgyj.popupmoah.popupstore.application.dto.PopupStoreLocationResponse;
import com.sgyj.popupmoah.popupstore.application.service.PopupStoreApplicationService;
import com.sgyj.popupmoah.popupstore.domain.entity.PopupStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 팝업스토어 웹 컨트롤러
 * HTTP 요청을 처리하는 인프라스트럭처 어댑터
 */
@Slf4j
@RestController
@RequestMapping("/api/popupstores")
@RequiredArgsConstructor
@Tag(name = "팝업스토어 관리", description = "팝업스토어 CRUD 및 관련 기능 API")
public class PopupStoreController {
    
    private final PopupStoreApplicationService applicationService;
    
    /**
     * 팝업스토어를 생성합니다.
     */
    @Operation(summary = "팝업스토어 생성", description = "새로운 팝업스토어를 생성합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "201", description = "팝업스토어 생성 성공"),
            @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PopupStoreResponse>> createPopupStore(@Valid @RequestBody PopupStoreCreateRequest request) {
        log.info("팝업스토어 생성 API 호출: name={}", request.getName());
        
        try {
            PopupStore popupStore = PopupStore.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .imageUrl(request.getImageUrl())
                    .sourceUrl(request.getSourceUrl())
                    .category(request.getCategory())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .location(request.getLocation())
                    .active(true)
                    .viewCount(0L)
                    .likeCount(0L)
                    .build();
            
            PopupStore created = applicationService.createPopupStore(popupStore);
            PopupStoreResponse response = convertToResponse(created);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "팝업스토어가 성공적으로 생성되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("팝업스토어 생성 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * ID로 팝업스토어를 조회합니다.
     */
    @Operation(summary = "팝업스토어 조회", description = "ID로 특정 팝업스토어를 조회합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "팝업스토어 조회 성공"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStoreResponse>> getPopupStore(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id) {
        log.info("팝업스토어 조회 API 호출: id={}", id);
        
        return applicationService.getPopupStore(id)
                .map(popupStore -> {
                    PopupStoreResponse response = convertToResponse(popupStore);
                    return ResponseEntity.ok(ApiResponse.success(response));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 모든 팝업스토어를 조회합니다.
     */
    @Operation(summary = "전체 팝업스토어 조회", description = "모든 팝업스토어 목록을 조회합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "팝업스토어 목록 조회 성공"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PopupStoreResponse>>> getAllPopupStores() {
        log.info("전체 팝업스토어 조회 API 호출");
        
        List<PopupStore> popupStores = applicationService.getAllPopupStores();
        List<PopupStoreResponse> responses = popupStores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 활성화된 팝업스토어만 조회합니다.
     */
    @Operation(summary = "활성화된 팝업스토어 조회", description = "활성화된 팝업스토어 목록을 조회합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "활성화된 팝업스토어 목록 조회 성공"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PopupStoreResponse>>> getActivePopupStores() {
        log.info("활성화된 팝업스토어 조회 API 호출");
        
        List<PopupStore> popupStores = applicationService.getActivePopupStores();
        List<PopupStoreResponse> responses = popupStores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 팝업스토어를 업데이트합니다.
     */
    @Operation(summary = "팝업스토어 수정", description = "기존 팝업스토어 정보를 수정합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "팝업스토어 수정 성공"),
            @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PopupStoreResponse>> updatePopupStore(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id, 
            @Valid @RequestBody PopupStoreUpdateRequest request) {
        log.info("팝업스토어 수정 API 호출: id={}, name={}", id, request.getName());
        
        try {
            PopupStore popupStore = PopupStore.builder()
                    .id(id)
                    .name(request.getName())
                    .description(request.getDescription())
                    .imageUrl(request.getImageUrl())
                    .sourceUrl(request.getSourceUrl())
                    .category(request.getCategory())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .location(request.getLocation())
                    .build();
            
            PopupStore updated = applicationService.updatePopupStore(id, popupStore);
            PopupStoreResponse response = convertToResponse(updated);
            
            return ResponseEntity.ok(ApiResponse.success(response, "팝업스토어가 성공적으로 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("팝업스토어 수정 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 팝업스토어를 삭제합니다.
     */
    @Operation(summary = "팝업스토어 삭제", description = "팝업스토어를 삭제합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "팝업스토어 삭제 성공"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePopupStore(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id) {
        log.info("팝업스토어 삭제 API 호출: id={}", id);
        
        try {
            applicationService.deletePopupStore(id);
            return ResponseEntity.ok(ApiResponse.success(null, "팝업스토어가 성공적으로 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("팝업스토어 삭제 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 팝업스토어 조회수를 증가시킵니다.
     */
    @Operation(summary = "조회수 증가", description = "팝업스토어 조회수를 1 증가시킵니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "조회수 증가 성공"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id) {
        log.info("팝업스토어 조회수 증가 API 호출: id={}", id);
        
        try {
            applicationService.incrementViewCount(id);
            return ResponseEntity.ok(ApiResponse.success(null, "조회수가 증가되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("조회수 증가 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 팝업스토어 좋아요 수를 증가시킵니다.
     */
    @Operation(summary = "좋아요 증가", description = "팝업스토어 좋아요 수를 1 증가시킵니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "좋아요 증가 성공"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> incrementLikeCount(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id) {
        log.info("팝업스토어 좋아요 증가 API 호출: id={}", id);
        
        try {
            applicationService.incrementLikeCount(id);
            return ResponseEntity.ok(ApiResponse.success(null, "좋아요가 증가되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("좋아요 증가 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 팝업스토어 좋아요 수를 감소시킵니다.
     */
    @Operation(summary = "좋아요 감소", description = "팝업스토어 좋아요 수를 1 감소시킵니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "좋아요 감소 성공"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> decrementLikeCount(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id) {
        log.info("팝업스토어 좋아요 감소 API 호출: id={}", id);
        
        try {
            applicationService.decrementLikeCount(id);
            return ResponseEntity.ok(ApiResponse.success(null, "좋아요가 감소되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("좋아요 감소 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 팝업스토어를 활성화합니다.
     */
    @Operation(summary = "팝업스토어 활성화", description = "팝업스토어를 활성화합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "팝업스토어 활성화 성공"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activatePopupStore(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id) {
        log.info("팝업스토어 활성화 API 호출: id={}", id);
        
        try {
            applicationService.activatePopupStore(id);
            return ResponseEntity.ok(ApiResponse.success(null, "팝업스토어가 활성화되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("팝업스토어 활성화 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 팝업스토어를 비활성화합니다.
     */
    @Operation(summary = "팝업스토어 비활성화", description = "팝업스토어를 비활성화합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "팝업스토어 비활성화 성공"),
            @SwaggerApiResponse(responseCode = "404", description = "팝업스토어를 찾을 수 없음"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivatePopupStore(
            @Parameter(description = "팝업스토어 ID", required = true) @PathVariable Long id) {
        log.info("팝업스토어 비활성화 API 호출: id={}", id);
        
        try {
            applicationService.deactivatePopupStore(id);
            return ResponseEntity.ok(ApiResponse.success(null, "팝업스토어가 비활성화되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("팝업스토어 비활성화 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * PopupStore 엔티티를 PopupStoreResponse DTO로 변환
     */
    private PopupStoreResponse convertToResponse(PopupStore popupStore) {
        return PopupStoreResponse.builder()
                .id(popupStore.getId())
                .name(popupStore.getName())
                .description(popupStore.getDescription())
                .imageUrl(popupStore.getImageUrl())
                .sourceUrl(popupStore.getSourceUrl())
                .category(popupStore.getCategory())
                .startDate(popupStore.getStartDate())
                .endDate(popupStore.getEndDate())
                .location(popupStore.getLocation())
                .address(popupStore.getAddress())
                .latitude(popupStore.getLatitude())
                .longitude(popupStore.getLongitude())
                .active(popupStore.getActive())
                .viewCount(popupStore.getViewCount())
                .likeCount(popupStore.getLikeCount())
                .createdAt(popupStore.getCreatedAt())
                .updatedAt(popupStore.getUpdatedAt())
                .build();
    }

    /**
     * 팝업스토어 위치 정보를 조회합니다.
     */
    @Operation(summary = "팝업스토어 위치 정보 조회", description = "지도에 표시할 팝업스토어 위치 정보를 조회합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "위치 정보 조회 성공"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<PopupStoreLocationResponse>>> getPopupStoreLocations() {
        log.info("팝업스토어 위치 정보 조회 API 호출");
        
        try {
            List<PopupStore> popupStores = applicationService.getAllPopupStores();
            List<PopupStoreLocationResponse> locations = popupStores.stream()
                    .filter(PopupStore::hasCoordinates)
                    .map(this::convertToLocationResponse)
                    .collect(Collectors.toList());
            
            ApiResponse<List<PopupStoreLocationResponse>> response = ApiResponse.success(locations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("팝업스토어 위치 정보 조회 중 오류 발생", e);
            ApiResponse<List<PopupStoreLocationResponse>> response = ApiResponse.error("위치 정보 조회에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 활성화된 팝업스토어 위치 정보를 조회합니다.
     */
    @Operation(summary = "활성화된 팝업스토어 위치 정보 조회", description = "현재 활성화된 팝업스토어의 위치 정보를 조회합니다.")
    @ApiResponses(value = {
            @SwaggerApiResponse(responseCode = "200", description = "위치 정보 조회 성공"),
            @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/locations/active")
    public ResponseEntity<ApiResponse<List<PopupStoreLocationResponse>>> getActivePopupStoreLocations() {
        log.info("활성화된 팝업스토어 위치 정보 조회 API 호출");
        
        try {
            List<PopupStore> popupStores = applicationService.getActivePopupStores();
            List<PopupStoreLocationResponse> locations = popupStores.stream()
                    .filter(PopupStore::hasCoordinates)
                    .map(this::convertToLocationResponse)
                    .collect(Collectors.toList());
            
            ApiResponse<List<PopupStoreLocationResponse>> response = ApiResponse.success(locations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("활성화된 팝업스토어 위치 정보 조회 중 오류 발생", e);
            ApiResponse<List<PopupStoreLocationResponse>> response = ApiResponse.error("위치 정보 조회에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PopupStore 엔티티를 PopupStoreLocationResponse로 변환합니다.
     */
    private PopupStoreLocationResponse convertToLocationResponse(PopupStore popupStore) {
        return PopupStoreLocationResponse.builder()
                .id(popupStore.getId())
                .name(popupStore.getName())
                .address(popupStore.getAddress())
                .latitude(popupStore.getLatitude())
                .longitude(popupStore.getLongitude())
                .category(popupStore.getCategory())
                .active(popupStore.getActive())
                .imageUrl(popupStore.getImageUrl())
                .build();
    }
} 