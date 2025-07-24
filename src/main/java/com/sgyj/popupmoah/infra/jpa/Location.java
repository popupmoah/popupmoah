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

    @Override
    public boolean equals(Object o) {
        if(o instanceof Location l){
            return l.latitude == this.latitude && l.longitude == this.longitude;
        }
        return false;
    }
    
}
