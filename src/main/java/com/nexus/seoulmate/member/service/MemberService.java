package com.nexus.seoulmate.member.service;

import com.nexus.seoulmate.aws.service.AmazonS3Service;
import com.nexus.seoulmate.exception.CustomException;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.nexus.seoulmate.global.status.ErrorStatus.HOBBY_SAVE_FAILED;
import static com.nexus.seoulmate.global.status.ErrorStatus.UNAUTHORIZED;
import static com.nexus.seoulmate.global.status.ErrorStatus.USER_NOT_FOUND;

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
    public String uploadProfileImage(MultipartFile profileImage) {

        // S3 업로드 로직 구현
        return amazonS3Service.uploadProfile(profileImage).getUrl();
    }

    // 2. 언어 레벨 테스트
    // FluentProxyService 에서 진행
    public void submitLevelTest(LevelTestRequest levelTestRequest, String googleId){

        tempStorage.save(levelTestRequest, googleId);
    }

    // 3. 취미 선택
    public void selectHobby(HobbyRequest hobbyRequest, String googleId){
        
        List<Hobby> newHobbies = hobbyRepository.findByHobbyNameIn(hobbyRequest.getHobbies());

        if (newHobbies.size() != hobbyRequest.getHobbies().size()){
            throw new CustomException(HOBBY_SAVE_FAILED); // 존재하지 않는 취미가 포함되어 있습니다. 
        }

        tempStorage.save(hobbyRequest, googleId);
    }

    // 4. 학교 인증
    public void authUniv(UnivAuthDto univAuthRequest, String googleId){

        tempStorage.save(univAuthRequest, googleId);
    }

    // 4-1. 학교 인증서 업로드
    public String uploadUnivCertificate(MultipartFile profileImage) {
        // S3 업로드 로직 구현
        return amazonS3Service.uploadCertificate(profileImage).getUrl();

    }

    // 정보 다 합쳐서 회원가입 완료 + 모든 회원간의 궁합 생성하기
    @Transactional
    public void completeSignup(String googleId, HttpServletRequest request) {
        MemberCreateRequest memberCreateRequest = tempStorage.collect(googleId);

        List<Hobby> newHobbies = hobbyRepository.findByHobbyNameIn(memberCreateRequest.getHobbies());

        Member member = Member.createGoogleUser(
                memberCreateRequest.getEmail(),
                memberCreateRequest.getFirstName(),
                memberCreateRequest.getLastName(),
                memberCreateRequest.getDOB(),
                memberCreateRequest.getCountry(),
                memberCreateRequest.getBio(),
                memberCreateRequest.getProfileImage(),
                newHobbies,
                memberCreateRequest.getUnivCertificate(),
                memberCreateRequest.getUniv(),
                memberCreateRequest.getLanguages(),
                memberCreateRequest.getVerificationStatus()
        );

        // Member 저장
        memberRepository.save(member);

        Optional<GoogleInfo> existingGoogleInfo = googleInfoRepository.findByGoogleId(googleId);

        if (existingGoogleInfo.isPresent()) {
            GoogleInfo googleInfo = existingGoogleInfo.get();
            googleInfo.saveUserId(member);
            googleInfoRepository.save(googleInfo);
            System.out.println("GoogleInfo 테이블에 userId 저장 완료");
        } else {
            throw new CustomException(USER_NOT_FOUND);
        }

        // JSESSIONID 추출
        String jsessionId = extractJsessionId(request);

        System.out.println(member);
        System.out.println(jsessionId);
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

        return memberRepository.findByEmailWithDetails(email)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    // 현재 로그인한 사용자의 userId만 반환
    public Long getCurrentId() {
        return getCurrentUser().getUserId();
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

    // 프로필 이미지 조회용 메서드
    public String findProfileImageUrlById(Long userId){
        return memberRepository.findProfileImageById(userId).orElse(null);
    }
}

