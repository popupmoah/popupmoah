package com.sgyj.popupmoah.module.popupstore.controller.rseponse;

import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import lombok.Getter;

@Getter
public class PopupStoreCreatedResponse extends PopupStoreDefaultResponse{

    public static PopupStoreCreatedResponse fromDto(PopupStoreDto popupStore) {
        PopupStoreCreatedResponse response = new PopupStoreCreatedResponse();
        return (PopupStoreCreatedResponse) getPopupStoreDefaultResponse(popupStore, response);
    }

}
