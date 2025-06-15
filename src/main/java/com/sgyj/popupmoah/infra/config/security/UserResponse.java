package com.sgyj.popupmoah.infra.config.security;

import lombok.Builder;

@Builder
public class UserResponse {

    private String userId;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String provider;
    private String providerId;
    private String role;
}