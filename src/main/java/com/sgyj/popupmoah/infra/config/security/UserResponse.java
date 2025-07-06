package com.sgyj.popupmoah.infra.config.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserResponse {

    private String userId;
    private String username;
    private String lastName;
    private String firstName;
    private String password;
    private String email;
    private String phone;
    private String provider;
    private String providerId;
    private String role;
}