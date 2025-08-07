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

    private final MemberService memberService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping
    @Operation(summary = "서울메이트 메인페이지지 조회", description = "인증된 사용자의 서울메이트 메인 정보를 조회합니다.")
    @SecurityRequirement(name = "sessionId")
    public Response<Map<String, Object>> getSeoulmateInfo(HttpServletRequest request) {
        try {
            Member currentUser = memberService.getCurrentUser();

            Map<String, Object> data = new HashMap<>();
            data.put("email", currentUser.getEmail());
            data.put("memberId", currentUser.getUserId());
            data.put("role", currentUser.getRole());
            data.put("schoolVerification", currentUser.getUnivVerification());

            // JSESSIONID 쿠키 찾기
            customOAuth2UserService.changeJsessionId(request);
            String jsessionId = memberService.getSessionId(request);
            data.put("jsessionId", "JSESSIONID=" + jsessionId);

            return Response.success(SuccessStatus.SUCCESS, data);
        } catch (Exception e) {
            return Response.fail(ErrorStatus.UNAUTHORIZED);
        }
    }
}