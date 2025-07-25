package com.sgyj.popupmoah.module.community.entity;

import com.sgyj.popupmoah.module.popupstore.entity.PopupStore;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
// Removed @Setter to prevent uncontrolled mutations.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_store_id", nullable = false)
    private PopupStore popupStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }
} 