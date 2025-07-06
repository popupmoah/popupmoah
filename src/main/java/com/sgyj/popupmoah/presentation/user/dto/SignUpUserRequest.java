package com.sgyj.popupmoah.presentation.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpUserRequest {

    @NotNull
    private String lastName;
    @NotNull
    private String firstName;
    @NotNull
    private String userId;
    @NotNull
    private String password;
    @NotNull
    private String confirmPassword;
    @Email
    private String email;
    @NotNull
    private String phone;
}
