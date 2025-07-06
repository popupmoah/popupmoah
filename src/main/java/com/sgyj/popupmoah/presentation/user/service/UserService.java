package com.sgyj.popupmoah.presentation.user.service;

import com.sgyj.popupmoah.infra.config.exceptions.AlreadyExistInfoException;
import com.sgyj.popupmoah.infra.config.exceptions.PasswordMismatchException;
import com.sgyj.popupmoah.infra.config.security.UserResponse;
import com.sgyj.popupmoah.presentation.user.dto.SignUpUserRequest;
import com.sgyj.popupmoah.presentation.user.entity.User;
import com.sgyj.popupmoah.presentation.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * 사용자 회원가입
     *
     * @param request
     * @return
     */
    @Transactional
    public UserResponse registerUser(@Valid SignUpUserRequest request) {
        validationUser(request);

        String encryptPassword = passwordEncoder.encode(request.getPassword());
        User user = User.from(request, encryptPassword);
        User entityUser = userRepository.save(user);

        return entityUser.toResponse();
    }

    /**
     * 사용자 검증
     *
     * @param request
     */
    private void validationUser(@Valid SignUpUserRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        boolean existsUserByEmail = userRepository.existsUserByEmail(request.getEmail());
        if (existsUserByEmail) {
            throw new AlreadyExistInfoException("이미 등록된 이메일이 있습니다.");
        }

        boolean existsUserByPhone = userRepository.existsUserByPhone(request.getPhone());
        if (existsUserByPhone) {
            throw new AlreadyExistInfoException("이미 등록된 전화번호가 있습니다.");
        }
    }
}
