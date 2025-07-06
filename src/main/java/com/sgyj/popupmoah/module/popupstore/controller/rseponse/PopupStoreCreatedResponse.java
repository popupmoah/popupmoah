package com.sgyj.popupmoah.module.popupstore.controller.rseponse;

import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import lombok.Getter;

@Getter
public class PopupStoreCreatedResponse {

    private Long id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String reservationDate;

    public static PopupStoreCreatedResponse fromDto(PopupStoreDto popupStore) {
        PopupStoreCreatedResponse response = new PopupStoreCreatedResponse();
        response.id = popupStore.getId();
        response.name = popupStore.getName();
        response.description = popupStore.getDescription();
        response.startDate = popupStore.getStartDate();
        response.endDate = popupStore.getEndDate();
        response.reservationDate = popupStore.getReservationDate();
        return response;
    }

}
