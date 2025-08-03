package com.sgyj.popupmoah.presentation.user.entity;

import com.sgyj.popupmoah.infra.config.security.UserResponse;
import com.sgyj.popupmoah.presentation.user.dto.SignUpUserRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User {

    @Id
    private String userId;

    private String password;

    private String lastName;

    private String firstName;

    private String email;

    private String phone;

    public User(@NotNull String password, @NotNull String userId, @NotNull String lastName, @NotNull String firstName, @Email String email, @NotNull String phone) {
        this.password = password;
        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phone = phone;
    }


    public static User from(SignUpUserRequest request, String password) {
        return new User(password, request.getUserId(), request.getLastName(), request.getFirstName(), request.getEmail(), request.getPhone());
    }

    public UserResponse toResponse() {
        return UserResponse.builder()
                .userId(userId)
                .lastName(lastName)
                .firstName(firstName)
                .email(email)
                .phone(phone)
                .build();
    }
}
