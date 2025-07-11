package com.sgyj.popupmoah.module.popupstore.controller.rseponse;

import com.sgyj.popupmoah.module.popupstore.dto.PopupStoreDto;
import lombok.Getter;

@Getter
public class PopupStoreSearchedResponse extends PopupStoreDefaultResponse {


    public static PopupStoreSearchedResponse fromDto(PopupStoreDto popupStore) {
        PopupStoreSearchedResponse response = new PopupStoreSearchedResponse();
        return (PopupStoreSearchedResponse) getPopupStoreDefaultResponse(popupStore, response);
    }

}
