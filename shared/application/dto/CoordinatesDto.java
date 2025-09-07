package com.sgyj.popupmoah.shared.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 좌표 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDto {

    private Double latitude;
    private Double longitude;

    /**
     * 좌표가 유효한지 확인
     */
    public boolean isValid() {
        return latitude != null && longitude != null 
            && latitude >= -90 && latitude <= 90 
            && longitude >= -180 && longitude <= 180;
    }

    /**
     * 두 좌표 간의 거리 계산 (Haversine 공식)
     * @param other 다른 좌표
     * @return 거리 (미터)
     */
    public double distanceTo(CoordinatesDto other) {
        if (!this.isValid() || !other.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 좌표입니다.");
        }

        final int R = 6371000; // 지구 반지름 (미터)
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLatRad = Math.toRadians(other.latitude - this.latitude);
        double deltaLonRad = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
