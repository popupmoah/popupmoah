package com.sgyj.popupmoah.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 수정 시간을 자동으로 관리하는 공통 엔티티
 * CreatedEntity를 상속받아 생성/수정 시간을 모두 관리합니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class UpdatedEntity extends Object {

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티가 마지막으로 수정된 시간을 반환합니다.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 엔티티가 마지막으로 수정된 시간을 설정합니다.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 