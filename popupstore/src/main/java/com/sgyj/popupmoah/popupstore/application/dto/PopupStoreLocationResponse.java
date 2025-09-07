package com.sgyj.popupmoah.popupstore.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 팝업스토어 위치 정보 응답 DTO
 * 지도에 표시하기 위한 최소한의 정보만 포함
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupStoreLocationResponse {

    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String category;
    private Boolean active;
    private String imageUrl;

    /**
     * 좌표 정보가 있는지 확인
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}
