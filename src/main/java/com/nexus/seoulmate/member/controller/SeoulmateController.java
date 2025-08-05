package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import com.nexus.seoulmate.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/seoulmate")
@RequiredArgsConstructor
@Tag(name = "서울메이트", description = "서울메이트 메인 API")
public class SeoulmateController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping("")
    @Operation(summary = "서울메이트 메인페이지지 조회", description = "인증된 사용자의 서울메이트 메인 정보를 조회합니다.")
    @SecurityRequirement(name = "sessionId")
    public Response<Map<String, Object>> getSeoulmateInfo(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 인증되지 않은 사용자 체크
        if (authentication == null || !authentication.isAuthenticated() || 
            !(authentication.getPrincipal() instanceof OAuth2User)) {
            return Response.fail(ErrorStatus.UNAUTHORIZED);
        }
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Optional<Member> member = memberRepository.findByEmail(email);
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "서울메이트 메인 페이지에 접속했습니다!");
        data.put("email", email);
        data.put("isAuthenticated", true);

        // JSESSIONID 쿠키 찾기
        customOAuth2UserService.changeJsessionId(request);
        String jsessionId = memberService.getSessionId(request);
        
        if (member.isPresent()) {
            data.put("memberId", member.get().getUserId());
            data.put("firstName", member.get().getFirstName());
            data.put("lastName", member.get().getLastName());
            data.put("role", member.get().getRole());
            data.put("schoolVerification", member.get().getUnivVerification());
            data.put("jsessionId", "JSESSIONID=" + jsessionId);
        }
        
        return Response.success(SuccessStatus.SUCCESS, data);
    }
} 