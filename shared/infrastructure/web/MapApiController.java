package com.sgyj.popupmoah.shared.infrastructure.web;

import com.sgyj.popupmoah.shared.application.dto.AddressDto;
import com.sgyj.popupmoah.shared.application.dto.CoordinatesDto;
import com.sgyj.popupmoah.shared.application.dto.PlaceSearchRequest;
import com.sgyj.popupmoah.shared.application.dto.PlaceSearchResponse;
import com.sgyj.popupmoah.shared.application.service.MapApiService;
import com.sgyj.popupmoah.shared.infrastructure.web.response.ApiResponse;
import com.sgyj.popupmoah.shared.infrastructure.web.response.SwaggerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 지도 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
@Tag(name = "지도 API", description = "지도 관련 API")
public class MapApiController {

    private final MapApiService mapApiService;

    /**
     * 주소를 좌표로 변환 (지오코딩)
     */
    @Operation(summary = "지오코딩", description = "주소를 좌표로 변환합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "지오코딩 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/geocode")
    public ResponseEntity<ApiResponse<CoordinatesDto>> geocode(
            @Parameter(description = "주소", required = true)
            @RequestParam String address) {
        
        log.info("지오코딩 API 호출: {}", address);
        
        try {
            CoordinatesDto coordinates = mapApiService.geocode(address);
            ApiResponse<CoordinatesDto> response = ApiResponse.success(coordinates);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("지오코딩 실패: {}", e.getMessage(), e);
            ApiResponse<CoordinatesDto> response = ApiResponse.error("주소를 좌표로 변환할 수 없습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 좌표를 주소로 변환 (역지오코딩)
     */
    @Operation(summary = "역지오코딩", description = "좌표를 주소로 변환합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "역지오코딩 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/reverse-geocode")
    public ResponseEntity<ApiResponse<AddressDto>> reverseGeocode(
            @Parameter(description = "위도", required = true)
            @RequestParam Double latitude,
            @Parameter(description = "경도", required = true)
            @RequestParam Double longitude) {
        
        log.info("역지오코딩 API 호출: {}, {}", latitude, longitude);
        
        try {
            CoordinatesDto coordinates = CoordinatesDto.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            
            AddressDto address = mapApiService.reverseGeocode(coordinates);
            ApiResponse<AddressDto> response = ApiResponse.success(address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("역지오코딩 실패: {}", e.getMessage(), e);
            ApiResponse<AddressDto> response = ApiResponse.error("좌표를 주소로 변환할 수 없습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 장소 검색
     */
    @Operation(summary = "장소 검색", description = "키워드로 장소를 검색합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "장소 검색 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PlaceSearchResponse>> searchPlaces(
            @Parameter(description = "장소 검색 요청", required = true)
            @Valid @RequestBody PlaceSearchRequest request) {
        
        log.info("장소 검색 API 호출: {}", request.getQuery());
        
        try {
            PlaceSearchResponse response = mapApiService.searchPlaces(request);
            ApiResponse<PlaceSearchResponse> apiResponse = ApiResponse.success(response);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("장소 검색 실패: {}", e.getMessage(), e);
            ApiResponse<PlaceSearchResponse> response = ApiResponse.error("장소를 검색할 수 없습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 주변 팝업스토어 검색
     */
    @Operation(summary = "주변 팝업스토어 검색", description = "현재 위치 주변의 팝업스토어를 검색합니다.")
    @SwaggerApiResponse(responseCode = "200", description = "주변 팝업스토어 검색 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "500", description = "서버 내부 오류")
    @GetMapping("/nearby-popupstores")
    public ResponseEntity<ApiResponse<PlaceSearchResponse>> searchNearbyPopupStores(
            @Parameter(description = "위도", required = true)
            @RequestParam Double latitude,
            @Parameter(description = "경도", required = true)
            @RequestParam Double longitude,
            @Parameter(description = "검색 반경 (미터)", required = false)
            @RequestParam(defaultValue = "1000") Integer radius,
            @Parameter(description = "페이지 번호", required = false)
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", required = false)
            @RequestParam(defaultValue = "20") Integer size) {
        
        log.info("주변 팝업스토어 검색 API 호출: {}, {}, 반경: {}m", latitude, longitude, radius);
        
        try {
            CoordinatesDto center = CoordinatesDto.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            
            PlaceSearchRequest request = PlaceSearchRequest.builder()
                    .query("팝업스토어")
                    .center(center)
                    .radius(radius)
                    .page(page)
                    .size(size)
                    .sort("distance")
                    .build();
            
            PlaceSearchResponse response = mapApiService.searchPlaces(request);
            ApiResponse<PlaceSearchResponse> apiResponse = ApiResponse.success(response);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("주변 팝업스토어 검색 실패: {}", e.getMessage(), e);
            ApiResponse<PlaceSearchResponse> response = ApiResponse.error("주변 팝업스토어를 검색할 수 없습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}