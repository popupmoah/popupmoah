package com.sgyj.popupmoah.module.popupstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 팝업스토어 요청 DTO
 * 팝업스토어 생성 및 수정 시 사용되는 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupStoreRequest {

    @NotBlank(message = "팝업스토어 이름은 필수입니다.")
    @Size(max = 100, message = "팝업스토어 이름은 100자를 초과할 수 없습니다.")
    private String name;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
    private String description;

    @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다.")
    private String address;

    private Double latitude;
    private Double longitude;

    @Size(max = 100, message = "카테고리는 100자를 초과할 수 없습니다.")
    private String category;

    @Size(max = 50, message = "매장 번호는 50자를 초과할 수 없습니다.")
    private String storeNumber;

    @Size(max = 100, message = "웹사이트는 100자를 초과할 수 없습니다.")
    private String website;

    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
    private String email;

    @Size(max = 200, message = "소스 URL은 200자를 초과할 수 없습니다.")
    private String sourceUrl;

    @Size(max = 200, message = "이미지 URL은 200자를 초과할 수 없습니다.")
    private String imageUrl;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime reservationDate;

    @Builder.Default
    private Boolean isActive = true;

    private List<String> images;
}

