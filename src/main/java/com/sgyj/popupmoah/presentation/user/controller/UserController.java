package com.sgyj.popupmoah.presentation.user.controller;

import com.sgyj.popupmoah.infra.config.exceptions.NaverCallBackException;
import com.sgyj.popupmoah.infra.config.security.UserResponse;
import com.sgyj.popupmoah.presentation.user.dto.SignUpUserRequest;
import com.sgyj.popupmoah.presentation.user.service.TokenService;
import com.sgyj.popupmoah.presentation.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid SignUpUserRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/naver/callback")
    public ResponseEntity<UserResponse> callbackByNaver(@RequestBody Map<String, String> request) {
        String error = request.get("error");
        if (error != null) {
            throw new NaverCallBackException(request.get("error_description"));
        }

        tokenService.getAccessToken(request.get("code"));

        return ResponseEntity.ok(new UserResponse());
    }

    @GetMapping("/")
    public ResponseEntity<UserResponse> login(@AuthenticationPrincipal OidcUser user) {
        OidcUserInfo userInfo = user.getUserInfo();
        return ResponseEntity.ok(UserResponse.builder()
                .username(userInfo.getFullName())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhoneNumber())
                .build());
    }

}
