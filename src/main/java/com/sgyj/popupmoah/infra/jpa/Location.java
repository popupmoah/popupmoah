package com.sgyj.popupmoah.infra.jpa;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Location {

    private double latitude;
    private double longitude;

    public static Location of(double latitude, double longitude) {
        Location location = new Location();
        location.latitude = latitude;
        location.longitude = longitude;
        return location;
    }
}
