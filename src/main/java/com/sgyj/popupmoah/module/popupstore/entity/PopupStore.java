package com.sgyj.popupmoah.module.popupstore.entity;

import com.sgyj.popupmoah.infra.generator.IdGenerator;
import com.sgyj.popupmoah.infra.jpa.Location;
import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PopupStore extends UpdatedEntity {

    @Id
    @IdGenerator
    private Long id;

    private String name;

    private String description;

    @Embedded
    private Location location;

    /**
     * 팝업스토어 시작일시
     */
    private LocalDateTime startDate;

    /**
     * 팝업스토어 종료일시
     */
    private LocalDateTime endDate;

    /**
     * 팝업스토어 사전 예약 일시
     */
    private LocalDateTime reservationDate;

    /**
     * 팝업스토어 전화번호
     */
    private String storeNumber;

    /**
     * 팝업스토어 대표 이메일
     */
    private String email;

    /**
     * 팝업스토어 웹사이트
     */
    private String website;

    /**
     * 팝업스토어 대표 이미지 URL
     */
    private String imageUrl;

    /**
     * 팝업스토어 정보 출처(원본 게시글 링크)
     */
    private String sourceUrl;

    /**
     * 팝업스토어 카테고리/태그
     */
    private String category;

}
