package com.sgyj.popupmoah.module.community.repository;

import com.sgyj.popupmoah.module.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
} 