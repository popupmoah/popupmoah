package com.sgyj.popupmoah.module.popupstore.dto;

import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import lombok.Getter;

@Getter
public class PopupStoreDto {

    private Long id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String reservationDate;
    private double latitude;
    private double longitude;
    private String storeNumber;
    private String email;
    private String website;

    // Getters and Setters

    public static PopupStoreDto fromEntity(PopupStore popupStore) {
        PopupStoreDto dto = new PopupStoreDto();
        dto.id = popupStore.getId();
        dto.name = popupStore.getName();
        dto.description = popupStore.getDescription();
        dto.startDate = popupStore.getStartDate().toString();
        dto.endDate = popupStore.getEndDate().toString();
        dto.reservationDate = popupStore.getReservationDate().toString();
        dto.latitude = popupStore.getLocation().getLatitude();
        dto.longitude = popupStore.getLocation().getLongitude();
        dto.storeNumber = popupStore.getStoreNumber();
        dto.email = popupStore.getEmail();
        dto.website = popupStore.getWebsite();
        return dto;
    }
}
