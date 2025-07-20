package com.sgyj.popupmoah.module.community.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.sgyj.popupmoah.module.community.entity.Member;
import com.sgyj.popupmoah.module.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final MemberRepository memberRepository;

    @Cacheable(value = "userCache", key = "#username")
    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }
} 