package com.sgyj.popupmoah.module.community.entity;

import com.sgyj.popupmoah.infra.jpa.UpdatedEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 회원(사용자) 정보를 저장하는 엔티티.
 * username은 고유하며, password는 암호화되어 저장됨.
 */
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends UpdatedEntity {
    
    /**
     * 회원 고유 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 아이디(로그인용, 고유)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 암호화된 비밀번호
     */
    @Column(nullable = false)
    private String password;

    /**
     * 이메일 주소
     */
    @Column(unique = true, length = 100)
    private String email;

    /**
     * 닉네임
     */
    @Column(length = 50)
    private String nickname;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    /**
     * 회원 정보 업데이트
     */
    public void updateProfile(String nickname, String email, String profileImageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
} 