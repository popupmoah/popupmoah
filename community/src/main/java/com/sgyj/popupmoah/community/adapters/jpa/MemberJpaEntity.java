package com.sgyj.popupmoah.community.adapters.jpa;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 멤버 JPA 엔티티
 * 데이터베이스 매핑을 위한 인프라스트럭처 엔티티
 */
@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJpaEntity extends UpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 50)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
} 