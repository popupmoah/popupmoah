package com.sgyj.popupmoah.module.popupstore.controller.request;

import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreatePopupStoreRequest {

    /**
     * 팝업스토어 이름
     */
    private String popupStoreName;

    /**
     * 팝업스토어 설명
     */
    private String popupStoreDescription;

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

    private double latitude;

    private double longitude;

    /**
     * 팝업스토어 전화번호
     */
    private String storeNumber;
    /**
     * 팝업스토어 이메일
     */
    private String email;
    /**
     * 팝업스토어 웹사이트
     */
    private String website;

    public PopupStore mapToEntity() {

        return PopupStore.builder()
                .name(popupStoreName)
                .description(popupStoreDescription)
                .startDate(startDate)
                .endDate(endDate)
                .reservationDate(reservationDate)
                .latitude(latitude)
                .longitude(longitude)
                .storeNumber(storeNumber)
                .email(email)
                .website(website)
                .build();
    }

}
