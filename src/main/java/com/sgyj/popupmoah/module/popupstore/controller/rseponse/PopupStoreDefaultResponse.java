package com.sgyj.popupmoah.module.popupstore.controller.rseponse;

import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import lombok.Getter;

@Getter
public class PopupStoreDefaultResponse {

    protected Long id;
    protected String name;
    protected String description;
    protected String startDate;
    protected String endDate;
    protected String reservationDate;


    public static PopupStoreDefaultResponse getPopupStoreDefaultResponse(PopupStoreDto popupStore, PopupStoreDefaultResponse response) {
        response.id = popupStore.getId();
        response.name = popupStore.getName();
        response.description = popupStore.getDescription();
        response.startDate = popupStore.getStartDate();
        response.endDate = popupStore.getEndDate();
        response.reservationDate = popupStore.getReservationDate();
        return response;
    }

}
