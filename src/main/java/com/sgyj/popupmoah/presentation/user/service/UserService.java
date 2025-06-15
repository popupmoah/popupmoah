package com.sgyj.popupmoah.presentation.user.service;

import com.sgyj.popupmoah.infra.config.exceptions.PasswordMismatchException;
import com.sgyj.popupmoah.infra.config.security.UserResponse;
import com.sgyj.popupmoah.presentation.user.dto.SignUpUserRequest;
import com.sgyj.popupmoah.presentation.user.entity.User;
import com.sgyj.popupmoah.presentation.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public UserResponse registerUser(@Valid SignUpUserRequest request) {
        validationUser(request);

        User user = User.from(request);
        User entityUser = userRepository.save(user);

        return entityUser.toResponse();
    }

    private void validationUser(@Valid SignUpUserRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }
}
