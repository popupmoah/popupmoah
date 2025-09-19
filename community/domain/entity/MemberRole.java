package com.sgyj.popupmoah.community.domain.entity;

/**
 * 회원 권한을 정의하는 enum
 */
public enum MemberRole {
    USER("일반 사용자"),
    ADMIN("관리자");

    private final String description;

    MemberRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



