package com.sgyj.popupmoah.module.popupstore.entity;

import com.sgyj.popupmoah.infra.generator.IdGenerator;
import com.sgyj.popupmoah.infra.jpa.Location;
import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopupStore extends UpdatedEntity {

    @Id
    @IdGenerator
    private Long id;

    private String name;

    private String description;

    @Embedded
    private Location location;

}
