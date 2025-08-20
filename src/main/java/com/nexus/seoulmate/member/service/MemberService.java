package com.nexus.seoulmate.member.service;

import com.nexus.seoulmate.aws.service.AmazonS3Service;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.dto.InProgressResponse;
import com.nexus.seoulmate.member.dto.signup.*;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.repository.GoogleInfoRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.nexus.seoulmate.exception.status.ErrorStatus.UNAUTHORIZED;
import static com.nexus.seoulmate.exception.status.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TempStorage tempStorage;
    private final MemberRepository memberRepository;
    private final HobbyRepository hobbyRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final AmazonS3Service amazonS3Service;
    private final GoogleInfoRepository googleInfoRepository;

    // 1. 프로필 생성
    public void saveProfile(ProfileCreateRequest profileCreateRequest, String googleId){

        tempStorage.save(profileCreateRequest, googleId);
    }

    // 1-1. 프로필 이미지 업로드
    public String uploadProfileImage(String googleId, MultipartFile profileImage) {

        // S3 업로드 로직 구현
        return amazonS3Service.uploadProfile(profileImage).getUrl();

        // 임시로 파일명 반환
        // return "https://seoulmate-s3-bucket.s3.amazonaws.com/profile/" + googleId + "/" + profileImage.getOriginalFilename();
    }

    // 2. 언어 레벨 테스트
    // FluentProxyService 에서 진행
    public void submitLevelTest(LevelTestRequest levelTestRequest, String googleId){

        tempStorage.save(levelTestRequest, googleId);
    }

    // 3. 취미 선택
    public void selectHobby(HobbyRequest hobbyRequest, String googleId){
        tempStorage.save(hobbyRequest, googleId);
    }

    // 4. 학교 인증
    public void authUniv(UnivAuthDto univAuthRequest, String googleId){

        tempStorage.save(univAuthRequest, googleId);
    }

    // 4-1. 학교 인증서 업로드
    public String uploadUnivCertificate(String googleId, MultipartFile profileImage) {
        // S3 업로드 로직 구현
        return amazonS3Service.uploadCertificate(profileImage).getUrl();
        
        // 임시로 파일명 반환
        // return "https://seoulmate-s3-bucket.s3.amazonaws.com/certificate/" + googleId + "/" + profileImage.getOriginalFilename();
    }

    // 정보 다 합쳐서 회원가입 완료 + 모든 회원간의 궁합 생성하기
    public void completeSignup(String googleId, HttpServletRequest request) {
        MemberCreateRequest memberCreateRequest = tempStorage.collect(googleId);

        // 기존 Hobby 엔티티들을 조회
        List<Hobby> existingHobbies = new ArrayList<>();
        if (memberCreateRequest.getHobbies() != null) {
            for (Hobby hobby : memberCreateRequest.getHobbies()) {
                // hobbyName으로 기존 Hobby 엔티티 조회
                hobbyRepository.findByHobbyNameAndHobbyCategory(hobby.getHobbyName(), hobby.getHobbyCategory())
                        .ifPresent(existingHobbies::add);
            }
        }

        GoogleInfo googleInfoId = googleInfoRepository.findByGoogleId(googleId).
                orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        Member member = Member.createGoogleUser(
                googleInfoId,
                memberCreateRequest.getEmail(),
                memberCreateRequest.getFirstName(),
                memberCreateRequest.getLastName(),
                memberCreateRequest.getDOB(),
                memberCreateRequest.getCountry(),
                memberCreateRequest.getBio(),
                memberCreateRequest.getProfileImage(),
                existingHobbies,
                memberCreateRequest.getUnivCertificate(),
                memberCreateRequest.getUniv(),
                memberCreateRequest.getLanguages(),
                memberCreateRequest.getVerificationStatus(),
                memberCreateRequest.getAuthProvider()
        );

        // JSESSIONID 추출
        String jsessionId = extractJsessionId(request);

        System.out.println(member);
        System.out.println(jsessionId);

        // Member 저장
        memberRepository.save(member);
    }

    // 현재 로그인한 사용자의 학교 인증서 처리 상태 받기
    public InProgressResponse inProgress(HttpServletRequest request) {
        Member member = getCurrentUser();

        String jsessionId = getSessionId(request);
        customOAuth2UserService.changeJsessionId(request);

        return new InProgressResponse(
                member.getRole(),
                member.getUnivVerification(),
                "JSESSIONID=" + jsessionId
        );
    }

    private String extractJsessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 서버에서 JSESSIONID 추출하기
    public String getSessionId(HttpServletRequest request){
        return extractJsessionId(request);
    }

    // 현재 로그인한 사용자 정보 가져오기
    public Member getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof OAuth2User oAuth2User)) {
            throw new CustomException(UNAUTHORIZED);
        }

        String email = oAuth2User.getAttribute("email");

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    // 현재 로그인한 사용자의 이메일 가져오기
    public String getCurrentUserEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof OAuth2User oAuth2User)) {
            throw new CustomException(UNAUTHORIZED);
        }

        return oAuth2User.getAttribute("email");
    }

    // 현재 로그인한 사용자의 Google ID 가져오기
    public String getCurrentUserGoogleId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof OAuth2User oAuth2User)) {
            throw new CustomException(UNAUTHORIZED);
        }

        return oAuth2User.getAttribute("sub");
    }
}

