package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MemberRepository memberRepository;

    public AuthController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/status")
    public Response<Map<String, Object>> getAuthStatus(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof OAuth2User) {
            
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            
            // 데이터베이스에서 회원 정보 조회
            Optional<Member> member = memberRepository.findByEmail(email);
            
            Map<String, Object> data = new HashMap<>();
            
            if (member.isPresent()) {
                data.put("schoolVerification", member.get().getUnivVerification());
                data.put("email", email);
                data.put("firstName", member.get().getFirstName());
                data.put("lastName", member.get().getLastName());
                data.put("sessionId", session.getId());
                data.put("memberId", member.get().getUserId());
                data.put("role", member.get().getRole());
                data.put("isRegistered", true);
            } else {
                data.put("isRegistered", false);
                data.put("message", "회원가입이 완료되지 않았습니다.");
            }
            
            return Response.success(SuccessStatus.SUCCESS, data);
        } else {
            return Response.fail(ErrorStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public Response<Map<String, Object>> logout(HttpSession session) {
        Map<String, Object> data = new HashMap<>();
        
        if (session != null) {
            session.invalidate();
            data.put("message", "로그아웃이 완료되었습니다.");
            return Response.success(SuccessStatus.LOGOUT_SUCCESS, data);
        } else {
            return Response.fail(ErrorStatus.BAD_REQUEST);
        }
    }
} 