package com.sgyj.popupmoah.presentation.user.repository;

import com.sgyj.popupmoah.presentation.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsUserByEmail(@Email String email);

    boolean existsUserByPhone(@NotNull String phone);
}
