package com.sgyj.popupmoah.popupstore.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 팝업스토어 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupStoreUpdateRequest {

    @NotBlank(message = "팝업스토어명은 필수입니다.")
    @Size(max = 200, message = "팝업스토어명은 200자 이하여야 합니다.")
    private String name;

    @Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
    private String description;

    @Size(max = 500, message = "이미지 URL은 500자 이하여야 합니다.")
    private String imageUrl;

    @Size(max = 500, message = "소스 URL은 500자 이하여야 합니다.")
    private String sourceUrl;

    @Size(max = 100, message = "카테고리는 100자 이하여야 합니다.")
    private String category;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Size(max = 200, message = "위치는 200자 이하여야 합니다.")
    private String location;

    @Size(max = 500, message = "주소는 500자 이하여야 합니다.")
    private String address;

    private Double latitude;
    private Double longitude;
}
