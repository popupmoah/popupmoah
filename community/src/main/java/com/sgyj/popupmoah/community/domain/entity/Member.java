package com.sgyj.popupmoah.community.domain.entity;

import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import lombok.*;

/**
 * 멤버 도메인 엔티티
 * 순수 Java로 구현된 도메인 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends UpdatedEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private MemberRole role;

    public void updateProfile(String nickname, String email, String profileImageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    
    public void updateRole(MemberRole role) {
        this.role = role;
    }
} 