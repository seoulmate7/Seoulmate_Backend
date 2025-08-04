package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.member.domain.enums.Countries;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.domain.enums.University;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.dto.OAuth2Response;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.member.service.TempStorage;
import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.dto.signup.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static com.nexus.seoulmate.exception.status.ErrorStatus.*;
import static com.nexus.seoulmate.exception.status.SuccessStatus.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class MemberController {

    private final FluentProxyService fluentProxyService;
    private final MemberService memberService;
    private final TempStorage tempStorage;
    private final MemberRepository memberRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    // 소셜 회원가입

    // 1-1. 프로필 기본 정보 받아오기
    @GetMapping("/profile-info")
    public Response<Object> getProfileInfo(@AuthenticationPrincipal OAuth2User oAuth2User){
        System.out.println("=== 프로필 기본 정보 요청 ===");

        // loadUser 결과를 저장할 변수
        OAuth2User loadUserResult = null;

        if (oAuth2User instanceof CustomOAuth2User customUser) {
            OAuth2Response oAuth2Response = customUser.getOAuth2Response();
            System.out.println("ProviderId: " + oAuth2Response.getProviderId());

            // TempStorage에서 저장된 SignupResponse 가져오기
            SignupResponse dto = tempStorage.getSignupResponse(oAuth2Response.getProviderId());

            if (dto != null) {
                System.out.println("=== loadUser 결과 및 SignupResponse 정보 ===");
                System.out.println("세션 ID: " + dto.getSessionId());
                System.out.println("구글 ID: " + dto.getGoogleId());
                System.out.println("이메일: " + dto.getEmail());
                System.out.println("이름: " + dto.getFirstName());
                System.out.println("성: " + dto.getLastName());
                System.out.println("인증 제공자: " + dto.getAuthProvider());

                // SignupResponse를 data로 반환
                return Response.success(SuccessStatus.PROFILE_INFO_SUCCESS, dto);
            }
        }
        
        // SignupResponse가 없는 경우 빈 데이터 반환
        Map<String, Object> data = new HashMap<>();
        return Response.success(SuccessStatus.PROFILE_INFO_SUCCESS, data);
    }
    
    // 1-2. 프로필 생성 (DTO + 파일 동시 처리)
    @PostMapping("/create-profile")
    public Response<Object> createProfile(@RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("DOB") String DOB,
                                        @RequestParam("country") String country,
                                        @RequestParam("bio") String bio,
                                        @RequestPart(value = "profileImage") MultipartFile profileImage,
                                        @AuthenticationPrincipal OAuth2User oAuth2User){
        System.out.println("=== 프로필 생성 요청 ===");
        System.out.println("받은 firstName: " + firstName);
        System.out.println("받은 lastName: " + lastName);
        System.out.println("받은 DOB: " + DOB);
        System.out.println("받은 country: " + country);
        System.out.println("받은 bio: " + bio);
        
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);
        System.out.println("추출된 googleId: " + googleId);

        String profileImageUrl = memberService.uploadProfileImage(googleId, profileImage);
        
        // ProfileCreateRequest 객체 생성
        ProfileCreateRequest requestWithGoogleId = ProfileCreateRequest.builder()
                .googleId(googleId)
                .firstName(firstName)
                .lastName(lastName)
                .DOB(java.time.LocalDate.parse(DOB))
                .country(Countries.valueOf(country))
                .bio(bio)
                .profileImageUrl(profileImageUrl)
                .build();
        
        System.out.println("생성된 ProfileCreateRequest: " + requestWithGoogleId);
        System.out.println("업로드된 이미지: " + profileImageUrl);
        
        memberService.saveProfile(requestWithGoogleId, profileImage);
        System.out.println("프로필 저장 완료");
        return Response.success(SuccessStatus.PROFILE_SUCCESS, null);
    }

    // 2. 언어 레벨 테스트 - 점수 받기 (FluentProxyService)
    @GetMapping("/language/level-test")
    public Response<String> levelTest(@RequestPart("audioFile") MultipartFile audioFile,
                                      @RequestParam("language") Languages language) {
        System.out.println("=== 언어 레벨 테스트 요청 ===");
        System.out.println("업로드된 오디오 파일명: " + audioFile.getOriginalFilename());
        System.out.println("파일 크기: " + audioFile.getSize() + " bytes");
        System.out.println("테스트 언어: " + language);
        
        String result = fluentProxyService.fluentFlow(audioFile, language);
        System.out.println("Fluent API 결과: " + result);
        
        return Response.success(LEVEL_TEST_SUCCESS, result);
    }

    // 2. 언어 레벨 테스트 - 점수 저장하기
    @PostMapping("/language/level-test")
    public Response<Object> submitLevelTest(@RequestBody LevelTestRequest levelTestRequest,
                                          @AuthenticationPrincipal OAuth2User oAuth2User){
        System.out.println("=== 언어 레벨 테스트 점수 저장 요청 ===");
        System.out.println("받은 LevelTestRequest: " + levelTestRequest);
        
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);
        System.out.println("추출된 googleId: " + googleId);
        
        // 새로운 객체 생성하여 googleId 설정
        LevelTestRequest requestWithGoogleId = LevelTestRequest.builder()
                .googleId(googleId)
                .languages(levelTestRequest.getLanguages())
                .build();
        
        System.out.println("생성된 LevelTestRequest: " + requestWithGoogleId);
        
        memberService.submitLevelTest(requestWithGoogleId);
        System.out.println("언어 레벨 테스트 점수 저장 완료");
        return Response.success(SUBMIT_LEVEL_TEST_SUCCESS, null);
    }

    // 3. 취미 선택
    @PostMapping("/select-hobby")
    public Response<Object> selectHobby(@RequestBody HobbyRequest hobbyRequest,
                                       @AuthenticationPrincipal OAuth2User oAuth2User){
        System.out.println("=== 취미 선택 요청 ===");
        System.out.println("받은 HobbyRequest: " + hobbyRequest);
        
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);
        System.out.println("추출된 googleId: " + googleId);
        
        // 새로운 객체 생성하여 googleId 설정
        HobbyRequest requestWithGoogleId = new HobbyRequest(googleId, hobbyRequest.getHobbies());
        System.out.println("생성된 HobbyRequest: " + requestWithGoogleId);
        
        memberService.selectHobby(requestWithGoogleId);
        System.out.println("취미 선택 저장 완료");
        return Response.success(SuccessStatus.HOBBY_SUCCESS, null);
    }

    // 4. 학교 인증 + 최종 회원가입
    @PostMapping("/school")
    public Response<Object> authUniv(@RequestParam("university") University university,
                                    @RequestPart(value = "univCertificate") MultipartFile univCertificate,
                                    @AuthenticationPrincipal OAuth2User oAuth2User){
        System.out.println("=== 학교 인증 + 최종 회원가입 요청 ===");
        System.out.println("지원 학교: " + university);
        
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);
        System.out.println("추출된 googleId: " + googleId);

        String univCertificateUrl = memberService.uploadUnivCertificate(googleId, univCertificate);
        
        // 새로운 객체 생성하여 googleId 설정
        UnivAuthRequest requestWithGoogleId = UnivAuthRequest.builder()
                .googleId(googleId)
                .university(university)
                .univCertificate(univCertificateUrl)
                .build();
        
        System.out.println("생성된 UnivAuthRequest: " + requestWithGoogleId);
        
        memberService.authUniv(requestWithGoogleId);
        System.out.println("학교 인증 완료");
        
        memberService.completeSignup(googleId);
        System.out.println("최종 회원가입 완료");
        
        return Response.success(SuccessStatus.MEMBER_CREATED, null);
    }

    // OAuth2User에서 googleId 추출하는 헬퍼 메서드
    private String getGoogleIdFromOAuth2User(OAuth2User oAuth2User) {
        if (oAuth2User instanceof CustomOAuth2User) {
            CustomOAuth2User customUser =
                (CustomOAuth2User) oAuth2User;
            return customUser.getOAuth2Response().getProviderId();
        }
        return null;
    }

    @GetMapping("/in-progress")
    private Response<Object> inProgress(){

        Object result = memberService.getCurrentUser();

        if (result.equals(false)){
            return Response.fail(UNAUTHORIZED);
        } else {
            return Response.success(SuccessStatus.SUCCESS, result);
        }
    }
}
