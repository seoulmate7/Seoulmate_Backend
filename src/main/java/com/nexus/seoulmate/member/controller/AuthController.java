package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.dto.GoogleResponse;
import com.nexus.seoulmate.member.dto.OAuth2Response;
import com.nexus.seoulmate.member.dto.StatusResponse;
import com.nexus.seoulmate.member.dto.signup.SignupResponse;
import com.nexus.seoulmate.member.repository.GoogleInfoRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {

    private final MemberRepository memberRepository;
    private final GoogleInfoRepository googleInfoRepository;

    public AuthController(MemberRepository memberRepository, GoogleInfoRepository googleInfoRepository) {
        this.memberRepository = memberRepository;
        this.googleInfoRepository = googleInfoRepository;
    }

    @Operation(summary = "회원 상태 반환 API", description = "JSESSIONID로 회원 조회")
    @GetMapping("/status")
    public Response<StatusResponse> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof OAuth2User oAuth2User) {

            OAuth2Response oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            String email = oAuth2Response.getEmail();
            String googleId = oAuth2Response.getProviderId();

            Optional<Member> member = memberRepository.findByEmail(email);

            if (member.isPresent()) {
                Member user = member.get();
                Optional<GoogleInfo> googleInfoOpt = googleInfoRepository.findByGoogleId(googleId);
                String googleIdFromDb = googleInfoOpt.map(GoogleInfo::getGoogleId).orElse(null);

                StatusResponse statusResponse = new StatusResponse(
                        user.getUnivVerification(),
                        user.getUserStatus(),
                        googleIdFromDb
                );

                return Response.success(SuccessStatus.SUCCESS, statusResponse);
            } else {
                return Response.fail(ErrorStatus.MEMBER_NOT_FOUND);
            }
        } else {
            return Response.fail(ErrorStatus.UNAUTHORIZED);
        }
    }

    // 1-1. 수정된 프로필 기본 정보 받아오기
    @Operation(summary = "구글에서 받아온 정보 반환", description = "회원가입하지 않은 사용자의 리디렉션 경로")
    @GetMapping("/profile-info")
    public Response<SignupResponse> getProfileInfo(@AuthenticationPrincipal OAuth2User oAuth2User){
        System.out.println("=== 프로필 기본 정보 요청 ===");

        if (oAuth2User == null) {
            System.out.println("OAuth2User Principal이 null입니다. 인증되지 않았거나 세션이 만료되었습니다.");
            return Response.fail(ErrorStatus.UNAUTHORIZED);
        }

        // OAuth2User에서 providerId(googleId)만 추출
        String providerId = oAuth2User.getAttribute("sub");
        if (providerId == null) {
            System.out.println("ProviderId를 추출하지 못했습니다.");
            return Response.fail(ErrorStatus.BAD_REQUEST);
        }

        System.out.println("추출된 providerId: " + providerId);

        // GoogleInfo 테이블에서 Google ID로 모든 정보 조회
        Optional<GoogleInfo> googleInfoOpt = googleInfoRepository.findByGoogleId(providerId);

        if (googleInfoOpt.isPresent()) {
            GoogleInfo googleInfo = googleInfoOpt.get();
            System.out.println("GoogleInfo에서 사용자 정보 확인됨.");
            
            // SignupResponse DTO에 GoogleInfo의 모든 정보 담기
            SignupResponse responseDto = SignupResponse.builder()
                    .googleId(googleInfo.getGoogleId())
                    .email(googleInfo.getEmail())
                    .firstName(googleInfo.getFirstName())
                    .lastName(googleInfo.getLastName())
                    .sessionId(googleInfo.getSessionId())
                    .build();

            System.out.println("프로필 정보 반환 성공.");
            return Response.success(SuccessStatus.PROFILE_INFO_SUCCESS, responseDto);
        } else {
            System.out.println("GoogleInfo에서 providerId " + providerId + "에 해당하는 데이터를 찾을 수 없습니다.");
            return Response.fail(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }

    // 기존의 /auth/logout API는 변경 없습니다.
    @Operation(summary = "로그아웃 API")
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