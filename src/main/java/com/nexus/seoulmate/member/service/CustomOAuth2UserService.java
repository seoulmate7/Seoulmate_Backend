package com.nexus.seoulmate.member.service;

import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.domain.enums.Role;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.dto.GoogleResponse;
import com.nexus.seoulmate.member.dto.OAuth2Response;
import com.nexus.seoulmate.member.dto.signup.SignupResponse;
import com.nexus.seoulmate.member.repository.GoogleInfoRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final TempStorage tempStorage;
    private final GoogleInfoRepository googleInfoRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository, TempStorage tempStorage, GoogleInfoRepository googleInfoRepository){
        this.memberRepository = memberRepository;
        this.tempStorage = tempStorage;
        this.googleInfoRepository = googleInfoRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("=== CustomOAuth2UserService.loadUser 호출 ===");
        System.out.println("UserRequest의 ClientRegistrationId: " + userRequest.getClientRegistration().getRegistrationId());

        // 기본 OAuth2User 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("원본 OAuth2User 로드 완료. 속성: " + oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null; // DTO

        if ("google".equals(registrationId)) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            System.out.println("GoogleResponse DTO 생성 완료: " + oAuth2Response);
        } else {
            System.out.println("지원하지 않는 registrationId: " + registrationId);
            return null;
        }

        // DTO에서 주요 정보 추출
        String email = oAuth2Response.getEmail();
        String givenName = oAuth2Response.getGivenName();
        String familyName = oAuth2Response.getFamilyName();
        String providerId = oAuth2Response.getProviderId();

        System.out.println("=== OAuth2 처리 로그 ===");
        System.out.println("추출된 이메일: " + email);
        System.out.println("추출된 providerId: " + providerId);

        // 데이터베이스에서 회원 존재 여부 확인
        Optional<Member> existingUser = memberRepository.findByEmail(email);
        System.out.println("데이터베이스 조회 결과 - 기존 회원 존재 여부: " + existingUser.isPresent());

        if (existingUser.isEmpty()) { // 회원가입이 안 되어 있는 경우
            System.out.println("------------------------------------");
            System.out.println("--- 신규 사용자 회원가입 프로세스 시작 ---");
            System.out.println("------------------------------------");

            System.out.println("SignupResponse DTO 생성 중...");
            SignupResponse signupResponse = SignupResponse.builder()
                    .googleId(providerId)
                    .email(email)
                    .firstName(givenName)
                    .lastName(familyName)
                    .sessionId(null) // 세션 ID는 여기서 아직 알 수 없음
                    .build();
            System.out.println("생성된 SignupResponse: " + signupResponse);

            System.out.println("임시 저장소(TempStorage)에 SignupResponse 저장 중...");
            tempStorage.save(signupResponse);
            System.out.println("임시 저장 완료. key: " + providerId);
            System.out.println("임시 저장소에 SignupResponse가 잘 저장되었는지 확인 (get): " + tempStorage.getSignupResponse(providerId));

            // 임시로 USER 역할을 가진 CustomOAuth2User 반환
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2Response, Role.USER);
            System.out.println("신규 사용자를 위한 CustomOAuth2User 생성 및 반환. 역할: " + Role.USER);
            System.out.println("------------------------------------");
            System.out.println("--- 신규 사용자 회원가입 프로세스 종료 ---");
            System.out.println("------------------------------------");
            return customOAuth2User;
        } else { // 이미 회원가입이 되어 있는 경우
            System.out.println("------------------------------------");
            System.out.println("--- 기존 사용자 로그인 프로세스 시작 ---");
            System.out.println("------------------------------------");

            Member member = existingUser.get();
            Role role = member.getRole();
            System.out.println("기존 회원 로그인 성공. DB에 저장된 역할: " + role);

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2Response, role);
            System.out.println("기존 사용자를 위한 CustomOAuth2User 생성 및 반환. 역할: " + role);
            System.out.println("------------------------------------");
            System.out.println("--- 기존 사용자 로그인 프로세스 종료 ---");
            System.out.println("------------------------------------");
            return customOAuth2User;
        }
    }

    public void changeJsessionId(HttpServletRequest request){
        String jsessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    jsessionId = cookie.getValue();
                    break;
                }
            }
        }
        
        // 새로운 JSESSIONID 저장
        if (jsessionId != null) {
            // 기존에 같은 jsessionId가 있는지 확인
            Optional<GoogleInfo> existingGoogleInfo = googleInfoRepository.findBySessionId(jsessionId);
            
            // Optional이 비어있지 않은 경우에만 처리
            if (existingGoogleInfo.isPresent()) {
                GoogleInfo existing = existingGoogleInfo.get();
                existing.updateSessionId(jsessionId);  // setter 대신 도메인 메서드 사용
                googleInfoRepository.save(existing);
            }
        }
    }
}
