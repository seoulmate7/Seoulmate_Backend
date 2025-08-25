package com.nexus.seoulmate.member.handler;

import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.dto.OAuth2Response;
import com.nexus.seoulmate.member.repository.GoogleInfoRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleInfoRepository googleInfoRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("=== OAuth2 로그인 성공 핸들러 ===");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        OAuth2Response oAuth2Response = oAuth2User.getOAuth2Response();

        // 세션 ID와 GoogleId, 프로필 정보 추출
        String sessionId = request.getSession().getId();
        String googleId = oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();
        String firstName = oAuth2Response.getGivenName();
        String lastName = oAuth2Response.getFamilyName();

        System.out.println("로그인 성공: 세션 ID = " + sessionId + ", Google ID = " + googleId);

        Optional<GoogleInfo> existingGoogleInfo = googleInfoRepository.findByGoogleId(googleId);

        if (existingGoogleInfo.isPresent()) {
            GoogleInfo googleInfo = existingGoogleInfo.get();
            googleInfo.updateSessionId(sessionId);
            googleInfoRepository.save(googleInfo);
            System.out.println("기존 사용자 - GoogleInfo sessionId 업데이트 완료.");
        } else {
            // 신규 사용자: 모든 정보를 GoogleInfo에 저장
            GoogleInfo googleInfo = new GoogleInfo(sessionId, googleId, email, firstName, lastName);
            googleInfoRepository.save(googleInfo);
            System.out.println("신규 사용자 - GoogleInfo 객체 생성 및 저장 완료.");
        }
        
        // 리디렉션 경로
        response.sendRedirect("https://seoulmate-frontend.vercel.app/login/oauth2/code/google");

        // https://seoulmate-frontend.vercel.app/login/oauth2/code/google
        // http://localhost:5173/login/oauth2/code/google
        // https://seoulmate-frontend.vercel.app/login/oauth2/code/google

        // // 회원 상태에 따른 리디렉션
        // Optional<Member> member = memberRepository.findByEmail(email);
        // if (member.isEmpty()) {
        //     response.sendRedirect("/auth/profile-info");
        // } else {
        //     Member existingMember = member.get();
        //     if (VerificationStatus.SUBMITTED.equals(existingMember.getUnivVerification())) {
        //         response.sendRedirect("/signup/in-progress");
        //     } else {
        //         response.sendRedirect("/home");
        //     }
        // }
    }
}
