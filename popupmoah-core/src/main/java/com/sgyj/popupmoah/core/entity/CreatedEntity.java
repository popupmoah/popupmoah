package com.sgyj.popupmoah.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 생성 시간을 자동으로 관리하는 공통 엔티티
 * 모든 엔티티가 상속받아 사용할 수 있습니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * 엔티티가 생성된 시간을 반환합니다.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 엔티티가 생성된 시간을 설정합니다.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 