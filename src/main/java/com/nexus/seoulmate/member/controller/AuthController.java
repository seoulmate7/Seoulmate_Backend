package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.dto.StatusResponse;
import com.nexus.seoulmate.member.repository.GoogleInfoRepository;
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
    private final GoogleInfoRepository googleInfoRepository;

    public AuthController(MemberRepository memberRepository, GoogleInfoRepository googleInfoRepository) {
        this.memberRepository = memberRepository;
        this.googleInfoRepository = googleInfoRepository;
    }

    @GetMapping("/status")
    public Response<StatusResponse> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof OAuth2User oAuth2User) {

            String email = oAuth2User.getAttribute("email");

            Optional<Member> member = memberRepository.findByEmail(email);

            if (member.isPresent()) {
                Member user = member.get();
                Optional<GoogleInfo> googleIdOpt = googleInfoRepository.findByUserId(user);

                String googleId = googleIdOpt.map(info -> String.valueOf(info.getGoogleId())).orElse(null);

                StatusResponse statusResponse = new StatusResponse(
                        user.getUnivVerification(),
                        user.getUserStatus(),
                        googleId
                );

                return Response.success(SuccessStatus.SUCCESS, statusResponse);
            } else {
                return Response.fail(ErrorStatus.MEMBER_NOT_FOUND);
            }
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