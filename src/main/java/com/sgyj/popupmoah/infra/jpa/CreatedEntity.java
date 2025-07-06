package com.sgyj.popupmoah.infra.jpa;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedEntity {

    /**
     * 삭제 여부 ( 엔티티 공통으로 사용되는 변수로 CreatedEntity 는 항상 쓰이기 때문에 해당 클래스에 선언 )
     */
    private boolean deleted;

    @CreatedBy
    private Long createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    public void delete() {
        this.deleted = true;
    }

}
