package com.sgyj.popupmoah.reservation.adapters.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 예약 JPA 엔티티
 */
@Entity
@Table(name = "reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "popup_store_id", nullable = false)
    private Long popupStoreId;

    @Column(name = "member_name", nullable = false, length = 100)
    private String memberName;

    @Column(name = "member_email", nullable = false, length = 255)
    private String memberEmail;

    @Column(name = "member_phone", nullable = false, length = 20)
    private String memberPhone;

    @Column(name = "reservation_date_time", nullable = false)
    private LocalDateTime reservationDateTime;

    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "special_requests", length = 500)
    private String specialRequests;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


