package com.sgyj.popupmoah.shared.application.service;

import com.sgyj.popupmoah.shared.application.dto.AddressDto;
import com.sgyj.popupmoah.shared.application.dto.CoordinatesDto;
import com.sgyj.popupmoah.shared.application.dto.PlaceSearchRequest;
import com.sgyj.popupmoah.shared.application.dto.PlaceSearchResponse;
import com.sgyj.popupmoah.shared.infrastructure.config.MapApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 지도 API 서비스
 * 카카오맵과 네이버맵 API를 통합하여 제공하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapApiService {

    private final MapApiConfig mapApiConfig;
    private final RestTemplate restTemplate;

    /**
     * 주소를 좌표로 변환 (지오코딩)
     */
    public CoordinatesDto geocode(String address) {
        log.info("지오코딩 요청: {}", address);

        // 카카오맵 API로 먼저 시도
        try {
            return geocodeWithKakao(address);
        } catch (Exception e) {
            log.warn("카카오맵 지오코딩 실패, 네이버맵으로 재시도: {}", e.getMessage());
            try {
                return geocodeWithNaver(address);
            } catch (Exception ex) {
                log.error("지오코딩 실패: {}", ex.getMessage());
                throw new RuntimeException("주소를 좌표로 변환할 수 없습니다: " + address, ex);
            }
        }
    }

    /**
     * 좌표를 주소로 변환 (역지오코딩)
     */
    public AddressDto reverseGeocode(CoordinatesDto coordinates) {
        log.info("역지오코딩 요청: {}, {}", coordinates.getLatitude(), coordinates.getLongitude());

        // 카카오맵 API로 먼저 시도
        try {
            return reverseGeocodeWithKakao(coordinates);
        } catch (Exception e) {
            log.warn("카카오맵 역지오코딩 실패, 네이버맵으로 재시도: {}", e.getMessage());
            try {
                return reverseGeocodeWithNaver(coordinates);
            } catch (Exception ex) {
                log.error("역지오코딩 실패: {}", ex.getMessage());
                throw new RuntimeException("좌표를 주소로 변환할 수 없습니다", ex);
            }
        }
    }

    /**
     * 장소 검색
     */
    public PlaceSearchResponse searchPlaces(PlaceSearchRequest request) {
        log.info("장소 검색 요청: {}", request.getQuery());

        // 카카오맵 API로 먼저 시도
        try {
            return searchPlacesWithKakao(request);
        } catch (Exception e) {
            log.warn("카카오맵 장소 검색 실패, 네이버맵으로 재시도: {}", e.getMessage());
            try {
                return searchPlacesWithNaver(request);
            } catch (Exception ex) {
                log.error("장소 검색 실패: {}", ex.getMessage());
                throw new RuntimeException("장소를 검색할 수 없습니다: " + request.getQuery(), ex);
            }
        }
    }

    /**
     * 카카오맵 지오코딩
     */
    private CoordinatesDto geocodeWithKakao(String address) {
        if (mapApiConfig.getKakao().getApiKey() == null || mapApiConfig.getKakao().getApiKey().isEmpty()) {
            throw new RuntimeException("카카오맵 API 키가 설정되지 않았습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + mapApiConfig.getKakao().getApiKey());

        String url = UriComponentsBuilder.fromUriString(mapApiConfig.getKakao().getGeocodingUrl())
                .queryParam("query", address)
                .build()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
            
            if (documents != null && !documents.isEmpty()) {
                Map<String, Object> document = documents.get(0);
                String x = (String) document.get("x");
                String y = (String) document.get("y");
                
                return CoordinatesDto.builder()
                        .latitude(Double.parseDouble(y))
                        .longitude(Double.parseDouble(x))
                        .build();
            }
        }

        throw new RuntimeException("카카오맵 지오코딩 결과를 찾을 수 없습니다");
    }

    /**
     * 네이버맵 지오코딩
     */
    private CoordinatesDto geocodeWithNaver(String address) {
        if (mapApiConfig.getNaver().getClientId() == null || mapApiConfig.getNaver().getClientId().isEmpty()) {
            throw new RuntimeException("네이버맵 API 키가 설정되지 않았습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", mapApiConfig.getNaver().getClientId());
        headers.set("X-NCP-APIGW-API-KEY", mapApiConfig.getNaver().getClientSecret());

        String url = UriComponentsBuilder.fromUriString(mapApiConfig.getNaver().getGeocodingUrl())
                .queryParam("query", address)
                .build()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> addresses = (List<Map<String, Object>>) body.get("addresses");
            
            if (addresses != null && !addresses.isEmpty()) {
                Map<String, Object> addressInfo = addresses.get(0);
                String x = (String) addressInfo.get("x");
                String y = (String) addressInfo.get("y");
                
                return CoordinatesDto.builder()
                        .latitude(Double.parseDouble(y))
                        .longitude(Double.parseDouble(x))
                        .build();
            }
        }

        throw new RuntimeException("네이버맵 지오코딩 결과를 찾을 수 없습니다");
    }

    /**
     * 카카오맵 역지오코딩
     */
    private AddressDto reverseGeocodeWithKakao(CoordinatesDto coordinates) {
        if (mapApiConfig.getKakao().getApiKey() == null || mapApiConfig.getKakao().getApiKey().isEmpty()) {
            throw new RuntimeException("카카오맵 API 키가 설정되지 않았습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + mapApiConfig.getKakao().getApiKey());

        String url = UriComponentsBuilder.fromUriString("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                .queryParam("x", coordinates.getLongitude())
                .queryParam("y", coordinates.getLatitude())
                .build()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
            
            if (documents != null && !documents.isEmpty()) {
                Map<String, Object> document = documents.get(0);
                Map<String, Object> address = (Map<String, Object>) document.get("address");
                Map<String, Object> roadAddress = (Map<String, Object>) document.get("road_address");
                
                String fullAddress = (String) address.get("address_name");
                String roadAddressName = roadAddress != null ? (String) roadAddress.get("address_name") : null;
                
                return AddressDto.builder()
                        .fullAddress(fullAddress)
                        .roadAddress(roadAddressName)
                        .coordinates(coordinates)
                        .build();
            }
        }

        throw new RuntimeException("카카오맵 역지오코딩 결과를 찾을 수 없습니다");
    }

    /**
     * 네이버맵 역지오코딩
     */
    private AddressDto reverseGeocodeWithNaver(CoordinatesDto coordinates) {
        if (mapApiConfig.getNaver().getClientId() == null || mapApiConfig.getNaver().getClientId().isEmpty()) {
            throw new RuntimeException("네이버맵 API 키가 설정되지 않았습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", mapApiConfig.getNaver().getClientId());
        headers.set("X-NCP-APIGW-API-KEY", mapApiConfig.getNaver().getClientSecret());

        String url = UriComponentsBuilder.fromUriString("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc")
                .queryParam("coords", coordinates.getLongitude() + "," + coordinates.getLatitude())
                .queryParam("output", "json")
                .build()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
            
            if (results != null && !results.isEmpty()) {
                Map<String, Object> result = results.get(0);
                Map<String, Object> region = (Map<String, Object>) result.get("region");
                Map<String, Object> land = (Map<String, Object>) result.get("land");
                
                StringBuilder fullAddress = new StringBuilder();
                if (region != null) {
                    fullAddress.append(region.get("area1")).append(" ")
                              .append(region.get("area2")).append(" ")
                              .append(region.get("area3"));
                }
                if (land != null) {
                    fullAddress.append(" ").append(land.get("name"));
                }
                
                return AddressDto.builder()
                        .fullAddress(fullAddress.toString().trim())
                        .roadAddress(fullAddress.toString().trim())
                        .coordinates(coordinates)
                        .build();
            }
        }

        throw new RuntimeException("네이버맵 역지오코딩 결과를 찾을 수 없습니다");
    }

    /**
     * 카카오맵 장소 검색
     */
    private PlaceSearchResponse searchPlacesWithKakao(PlaceSearchRequest request) {
        if (mapApiConfig.getKakao().getApiKey() == null || mapApiConfig.getKakao().getApiKey().isEmpty()) {
            throw new RuntimeException("카카오맵 API 키가 설정되지 않았습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + mapApiConfig.getKakao().getApiKey());

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(mapApiConfig.getKakao().getPlacesUrl())
                .queryParam("query", request.getQuery())
                .queryParam("page", request.getPage() != null ? request.getPage() : 1)
                .queryParam("size", request.getSize() != null ? request.getSize() : 15);

        if (request.getCenter() != null) {
            builder.queryParam("x", request.getCenter().getLongitude())
                   .queryParam("y", request.getCenter().getLatitude());
        }

        if (request.getRadius() != null) {
            builder.queryParam("radius", request.getRadius());
        }

        if (request.getCategory() != null) {
            builder.queryParam("category_group_code", request.getCategory());
        }

        String url = builder.build().toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
            Map<String, Object> meta = (Map<String, Object>) body.get("meta");
            
            // PlaceInfo 객체로 변환하는 로직은 별도 메서드로 분리
            List<PlaceInfo> places = convertKakaoDocumentsToPlaceInfo(documents);
            
            return PlaceSearchResponse.builder()
                    .places(places)
                    .totalCount(meta != null ? (Integer) meta.get("total_count") : places.size())
                    .page(request.getPage() != null ? request.getPage() : 1)
                    .size(request.getSize() != null ? request.getSize() : 15)
                    .hasNext(meta != null && (Boolean) meta.get("is_end") == false)
                    .searchProvider("kakao")
                    .build();
        }

        throw new RuntimeException("카카오맵 장소 검색 결과를 찾을 수 없습니다");
    }

    /**
     * 네이버맵 장소 검색
     */
    private PlaceSearchResponse searchPlacesWithNaver(PlaceSearchRequest request) {
        if (mapApiConfig.getNaver().getClientId() == null || mapApiConfig.getNaver().getClientId().isEmpty()) {
            throw new RuntimeException("네이버맵 API 키가 설정되지 않았습니다");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", mapApiConfig.getNaver().getClientId());
        headers.set("X-NCP-APIGW-API-KEY", mapApiConfig.getNaver().getClientSecret());

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(mapApiConfig.getNaver().getPlacesUrl())
                .queryParam("query", request.getQuery())
                .queryParam("display", request.getSize() != null ? request.getSize() : 15)
                .queryParam("start", request.getPage() != null ? (request.getPage() - 1) * (request.getSize() != null ? request.getSize() : 15) + 1 : 1);

        if (request.getCenter() != null) {
            builder.queryParam("x", request.getCenter().getLongitude())
                   .queryParam("y", request.getCenter().getLatitude());
        }

        String url = builder.build().toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            
            // PlaceInfo 객체로 변환하는 로직은 별도 메서드로 분리
            List<PlaceInfo> places = convertNaverItemsToPlaceInfo(items);
            
            return PlaceSearchResponse.builder()
                    .places(places)
                    .totalCount(places.size())
                    .page(request.getPage() != null ? request.getPage() : 1)
                    .size(request.getSize() != null ? request.getSize() : 15)
                    .hasNext(false) // 네이버맵 API는 hasNext 정보를 제공하지 않음
                    .searchProvider("naver")
                    .build();
        }

        throw new RuntimeException("네이버맵 장소 검색 결과를 찾을 수 없습니다");
    }

    /**
     * 카카오맵 검색 결과를 PlaceInfo로 변환
     */
    private List<PlaceInfo> convertKakaoDocumentsToPlaceInfo(List<Map<String, Object>> documents) {
        // 구현 생략 - 실제로는 Map 데이터를 PlaceInfo 객체로 변환
        return List.of();
    }

    /**
     * 네이버맵 검색 결과를 PlaceInfo로 변환
     */
    private List<PlaceInfo> convertNaverItemsToPlaceInfo(List<Map<String, Object>> items) {
        // 구현 생략 - 실제로는 Map 데이터를 PlaceInfo 객체로 변환
        return List.of();
    }
}