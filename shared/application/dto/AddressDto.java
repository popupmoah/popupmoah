package com.sgyj.popupmoah.shared.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주소 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private String fullAddress;      // 전체 주소
    private String roadAddress;      // 도로명 주소
    private String jibunAddress;     // 지번 주소
    private String buildingName;     // 건물명
    private String region1DepthName; // 시/도
    private String region2DepthName; // 구/군
    private String region3DepthName; // 동/읍/면
    private String region4DepthName; // 리
    private String roadName;         // 도로명
    private String buildingNumber;   // 건물번호
    private String zoneCode;         // 우편번호
    private CoordinatesDto coordinates; // 좌표 정보
}

