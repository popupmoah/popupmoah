package com.sgyj.popupmoah.module.community.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 회원(사용자) 정보를 저장하는 엔티티.
 * username은 고유하며, password는 암호화되어 저장됨.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    /**
     * 회원 고유 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 아이디(로그인용, 고유)
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 암호화된 비밀번호
     */
    @Column(nullable = false)
    private String password;
} 