package com.nexus.seoulmate.member.handler;

import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.repository.GoogleInfoRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleInfoRepository googleInfoRepository;
    private final MemberRepository memberRepository;

    public CustomAuthenticationSuccessHandler(GoogleInfoRepository googleInfoRepository, MemberRepository memberRepository) {
        this.googleInfoRepository = googleInfoRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("=== OAuth2 로그인 성공 핸들러 ===");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 세션 ID와 GoogleId 추출
        String sessionId = request.getSession().getId();
        String googleId = oAuth2User.getAttributes().get("sub").toString();

        System.out.println("로그인 성공: 세션 ID = " + sessionId + ", Google ID = " + googleId);

        // GoogleInfo 테이블에서 기존 Google ID로 데이터 찾기
        Optional<GoogleInfo> existingGoogleInfo = googleInfoRepository.findByGoogleId(googleId);

        if (existingGoogleInfo.isPresent()) {
            // 기존 사용자: GoogleInfo의 sessionId를 업데이트
            GoogleInfo googleInfo = existingGoogleInfo.get();
            googleInfo.updateSessionId(sessionId);
            googleInfoRepository.save(googleInfo);
            System.out.println("기존 사용자 - GoogleInfo sessionId 업데이트 완료.");
        } else {
            // 신규 사용자: GoogleInfo를 새로 생성하고 저장
            GoogleInfo googleInfo = new GoogleInfo(sessionId, googleId);
            googleInfoRepository.save(googleInfo);
            System.out.println("신규 사용자 - GoogleInfo 객체 생성 및 저장 완료.");
        }

        // 회원 상태에 따른 리디렉션
        String email = oAuth2User.getOAuth2Response().getEmail();
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isEmpty()) {
            // DB에 회원이 없는 경우 (신규 회원)
            response.sendRedirect("/signup/profile-info");
        } else {
            Member existingMember = member.get();
            if (VerificationStatus.SUBMITTED.equals(existingMember.getUnivVerification())) {
                response.sendRedirect("/signup/in-progress");
            } else {
                response.sendRedirect("/home");
            }
        }
    }
}
