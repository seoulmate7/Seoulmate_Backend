package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/seoulmate")
public class SeoulmateController {

    private final MemberRepository memberRepository;

    public SeoulmateController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("")
    public Map<String, Object> getSeoulmateInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        Optional<Member> member = memberRepository.findByEmail(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "서울메이트 메인 페이지에 접속했습니다!");
        response.put("email", email);
        response.put("isAuthenticated", true);
        
        if (member.isPresent()) {
            response.put("memberId", member.get().getUserId());
            response.put("firstName", member.get().getFirstName());
            response.put("lastName", member.get().getLastName());
            response.put("role", member.get().getRole());
        }
        
        return response;
    }
} 