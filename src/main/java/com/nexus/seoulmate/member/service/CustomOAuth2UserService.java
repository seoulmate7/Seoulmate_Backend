package com.nexus.seoulmate.member.service;

import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.Role;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.dto.GoogleResponse;
import com.nexus.seoulmate.member.dto.OAuth2Response;
import com.nexus.seoulmate.member.dto.signup.SignupResponse;
import com.nexus.seoulmate.member.repository.MemberRepository;
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

    public CustomOAuth2UserService(MemberRepository memberRepository, TempStorage tempStorage){
        this.memberRepository = memberRepository;
        this.tempStorage = tempStorage;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null; // DTO

        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String email = oAuth2Response.getEmail();
        String givenName = oAuth2Response.getGivenName();
        String familyName = oAuth2Response.getFamilyName();

        Optional<Member> existingUser = memberRepository.findByEmail(email);
        System.out.println("=== OAuth2 처리 로그 ===");
        System.out.println("이메일: " + email);
        System.out.println("기존 회원 존재: " + existingUser.isPresent());

        if (existingUser.isEmpty()) { // 회원가입 안 되어있는 경우
            System.out.println("회원가입 안 된 사용자 - SignupResponse 생성");
            
            SignupResponse signupResponse = SignupResponse.builder()
                    .googleId(oAuth2Response.getProviderId())
                    .email(email)
                    .firstName(givenName)
                    .lastName(familyName)
                    .sessionId(null)
                    .build();

            // 임시 저장소에 구글 회원가입 정보 저장
            tempStorage.save(signupResponse);
            
            // 임시로 USER 역할을 가진 CustomOAuth2User 반환
            return new CustomOAuth2User(oAuth2Response, Role.USER);
        } else { // 회원가입 되어있는 경우
            System.out.println("기존 회원 - 로그인 처리");
            Member member = existingUser.get();
            Role role = member.getRole();
            System.out.println("기존 회원 로그인 성공 - 역할: " + role);

            return new CustomOAuth2User(oAuth2Response, role);
        }
    }
}
