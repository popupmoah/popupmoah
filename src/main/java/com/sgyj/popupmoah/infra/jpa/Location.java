package com.sgyj.popupmoah.infra.jpa;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Location {

    private double latitude;
    private double longitude;

}
