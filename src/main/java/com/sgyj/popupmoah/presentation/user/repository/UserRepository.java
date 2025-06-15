package com.sgyj.popupmoah.presentation.user.repository;

import com.sgyj.popupmoah.presentation.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
