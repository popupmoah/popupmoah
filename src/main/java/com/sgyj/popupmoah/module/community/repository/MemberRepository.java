package com.sgyj.popupmoah.module.community.repository;

import com.sgyj.popupmoah.module.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
} 