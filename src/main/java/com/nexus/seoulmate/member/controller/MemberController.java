package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.member.domain.enums.*;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.dto.InProgressResponse;
import com.nexus.seoulmate.member.dto.OAuth2Response;
import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.member.service.TempStorage;
import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.dto.signup.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.nexus.seoulmate.exception.status.SuccessStatus.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Tag(name = "회원가입", description = "회원가입 관련 API")
public class MemberController {

    private final FluentProxyService fluentProxyService;
    private final MemberService memberService;
    private final TempStorage tempStorage;
    private final CustomOAuth2UserService customOAuth2UserService;

    // 소셜 회원가입

    // 1-1. 프로필 기본 정보 받아오기
    @Operation(summary = "구글에서 받아온 정보 반환", description = "회원가입하지 않은 사용자의 리디렉션 경로")
    @GetMapping("/profile-info")
    public Response<Object> getProfileInfo(@AuthenticationPrincipal OAuth2User oAuth2User,
                                           HttpServletRequest request){
        System.out.println("=== 프로필 기본 정보 요청 ===");
        System.out.println("요청 URL: " + request.getRequestURL());
        System.out.println("세션 ID (요청): " + request.getSession(false));

        // SecurityContextHolder에서 직접 Principal 가져오기 시도
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("SecurityContextHolder 인증 객체: " + (authentication != null ? authentication.getClass().getName() : "null"));
        System.out.println("SecurityContextHolder Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));

        System.out.println("OAuth2User 타입 (@AuthenticationPrincipal): " + (oAuth2User != null ? oAuth2User.getClass().getName() : "null"));
        System.out.println("OAuth2User 인스턴스: " + oAuth2User);

        String providerId = null;
        String email = null;
        Object principal = null;

        if (oAuth2User != null) {
            principal = oAuth2User;
        } else if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            principal = authentication.getPrincipal();
            System.out.println("SecurityContextHolder에서 OAuth2User를 성공적으로 가져왔습니다.");
        }

        if (principal instanceof CustomOAuth2User customUser) {
            OAuth2Response oAuth2Response = customUser.getOAuth2Response();
            providerId = oAuth2Response.getProviderId();
            email = oAuth2Response.getEmail();
            System.out.println("CustomOAuth2User에서 추출한 providerId: " + providerId);
            System.out.println("CustomOAuth2User에서 추출한 이메일: " + email);
        } else if (principal instanceof OAuth2User normalOAuth2User) {
            providerId = normalOAuth2User.getAttribute("sub");
            email = normalOAuth2User.getAttribute("email");
            System.out.println("일반 OAuth2User에서 추출한 providerId: " + providerId);
            System.out.println("일반 OAuth2User에서 추출한 이메일: " + email);
        } else {
            System.out.println("Principal 객체가 OAuth2User 타입이 아닙니다. (혹은 null)");
        }

        if (providerId == null || email == null) {
            System.out.println("ProviderId 또는 Email을 추출하지 못했습니다. OAuth2 인증이 실패했거나 세션이 만료되었을 수 있습니다.");
            return Response.success(SuccessStatus.PROFILE_INFO_SUCCESS, Map.of("error", "Failed to get user info from OAuth2 principal."));
        }

        // TempStorage에서 저장된 SignupResponse 가져오기
        SignupResponse dto = tempStorage.getSignupResponse(providerId);

        if (dto != null) {
            System.out.println("TempStorage에서 SignupResponse를 찾았습니다. Google ID: " + dto.getGoogleId());
            String jsessionId = memberService.getSessionId(request);
            System.out.println("현재 요청의 JSESSIONID: " + jsessionId);
            customOAuth2UserService.changeJsessionId(request);
            String newJsessionId = memberService.getSessionId(request);
            System.out.println("changeJsessionId 호출 후 JSESSIONID: " + newJsessionId);

            // 쿠키를 포함한 새로운 SignupResponse 생성
            SignupResponse dtoWithCookies = SignupResponse.builder()
                    .googleId(dto.getGoogleId())
                    .email(dto.getEmail())
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .sessionId("JSESSIONID=" + jsessionId)
                    .build();

            System.out.println("프로필 정보 반환 성공.");
            return Response.success(SuccessStatus.PROFILE_INFO_SUCCESS, dtoWithCookies);
        } else {
            System.out.println("TempStorage에서 providerId " + providerId + "에 해당하는 SignupResponse를 찾을 수 없습니다.");
        }

        // SignupResponse가 없는 경우 빈 데이터 반환
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Temporary user data not found.");
        System.out.println("임시 데이터가 없으므로 빈 응답을 반환합니다.");
        return Response.success(SuccessStatus.PROFILE_INFO_SUCCESS, data);
    }

    // 1-2. 프로필 생성 (DTO + 파일 동시 처리)
    @Operation(summary = "1. 프로필 생성 API")
    @PostMapping(value = "/create-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Object> createProfile(@RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("DOB") String DOB,
                                        @RequestParam("country") String countryStr,
                                        @RequestParam("bio") String bio,
                                        @RequestPart(value = "profileImage") MultipartFile profileImage,
                                        @AuthenticationPrincipal OAuth2User oAuth2User){
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);
        System.out.println("추출된 googleId: " + googleId);

        String profileImageUrl = memberService.uploadProfileImage(googleId, profileImage);

        // String을 Countries enum으로 변환 (한글 표시명 또는 enum 상수명 모두 지원)
        Countries country;
        try {
            // 먼저 enum 상수명으로 시도
            country = Countries.valueOf(countryStr);
        } catch (IllegalArgumentException e) {
            // enum 상수명이 아니면 한글 표시명으로 시도
            country = Countries.fromDisplayName(countryStr);
        }

        // ProfileCreateRequest 객체 생성
        ProfileCreateRequest profileCreateRequest = new ProfileCreateRequest(
                firstName, lastName, LocalDate.parse(DOB), country, bio, profileImageUrl
        );
        
        memberService.saveProfile(profileCreateRequest, googleId);
        System.out.println("프로필 저장 완료");
        return Response.success(SuccessStatus.PROFILE_SUCCESS, null);
    }

    // 2. 언어 레벨 테스트 - 점수 받기 (FluentProxyService)
    @Operation(summary = "2-1. 언어 레벨 평가 API")
    @PostMapping(value = "/language/take-level-test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<String> levelTest(@RequestPart("audioFile") MultipartFile audioFile,
                                      @RequestParam("language") String languageStr) {
        System.out.println("=== 언어 레벨 테스트 요청 ===");
        System.out.println("업로드된 오디오 파일명: " + audioFile.getOriginalFilename());
        System.out.println("파일 크기: " + audioFile.getSize() + " bytes");
        System.out.println("테스트 언어: " + languageStr);
        
        // String을 Languages enum으로 변환 (한글 표시명 또는 enum 상수명 모두 지원)
        Languages language;
        try {
            // 먼저 enum 상수명으로 시도
            language = Languages.valueOf(languageStr);
        } catch (IllegalArgumentException e) {
            // enum 상수명이 아니면 한글 표시명으로 시도
            language = Languages.fromDisplayName(languageStr);
        }
        
        String result = fluentProxyService.fluentFlow(audioFile, language);
        System.out.println("Fluent API 결과: " + result);
        
        return Response.success(LEVEL_TEST_SUCCESS, result);
    }

    // 2. 언어 레벨 테스트 - 점수 저장하기
    @Operation(summary = "2-2. 언어 레벨 평가 결과 전송 API")
    @PostMapping("/language/submit-level-test")
    public Response<Object> submitLevelTest(@RequestBody LevelTestRequest levelTestRequest,
                                          @AuthenticationPrincipal OAuth2User oAuth2User){
        System.out.println("=== 언어 레벨 테스트 점수 저장 요청 ===");
        System.out.println("받은 LevelTestRequest: " + levelTestRequest);
        
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);
        
        memberService.submitLevelTest(levelTestRequest, googleId);
        System.out.println("언어 레벨 테스트 점수 저장 완료");
        return Response.success(SUBMIT_LEVEL_TEST_SUCCESS, null);
    }

    // 3. 취미 선택
    @Operation(summary = "3. 취미 선택 API")
    @PostMapping("/select-hobby")
    public Response<Object> selectHobby(@RequestBody HobbyRequest hobbyRequest,
                                       @AuthenticationPrincipal OAuth2User oAuth2User){
        
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);
        
        memberService.selectHobby(hobbyRequest, googleId);
        System.out.println("취미 선택 저장 완료");
        return Response.success(SuccessStatus.HOBBY_SUCCESS, null);
    }

    // 4. 학교 인증 + 최종 회원가입
    @Operation(summary = "4. 학교 인증 및 회원가입 API")
    @PostMapping(value = "/school", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Object> authUniv(@RequestParam("university") String universityStr,
                                    @RequestPart(value = "univCertificate") MultipartFile univCertificate,
                                    @AuthenticationPrincipal OAuth2User oAuth2User,
                                    HttpServletRequest request){
        
        // 현재 로그인한 사용자의 googleId 가져오기
        String googleId = getGoogleIdFromOAuth2User(oAuth2User);

        String univCertificateUrl = memberService.uploadUnivCertificate(googleId, univCertificate);

        // String을 University enum으로 변환 (한글 표시명 또는 enum 상수명 모두 지원)
        University university;
        try {
            // 먼저 enum 상수명으로 시도
            university = University.valueOf(universityStr);
        } catch (IllegalArgumentException e) {
            // enum 상수명이 아니면 한글 표시명으로 시도
            university = University.fromDisplayName(universityStr);
        }

        UnivAuthDto univAuthDto = UnivAuthDto.builder()
                .university(university)
                .univCertificateUrl(univCertificateUrl)
                .build();
        
        memberService.authUniv(univAuthDto, googleId);
        System.out.println("학교 인증 완료");
        
        memberService.completeSignup(googleId, request);
        System.out.println("최종 회원가입 완료");
        
        return Response.success(SuccessStatus.MEMBER_CREATED, null);
    }

    // OAuth2User에서 googleId 추출하는 헬퍼 메서드
    private String getGoogleIdFromOAuth2User(OAuth2User oAuth2User) {
        // oAuth2User가 null인 경우 SecurityContextHolder에서 가져오기 시도
        if (oAuth2User == null) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
                oAuth2User = (OAuth2User) authentication.getPrincipal();
                System.out.println("SecurityContextHolder에서 OAuth2User를 가져왔습니다.");
            }
        }
        
        if (oAuth2User instanceof CustomOAuth2User) {
            CustomOAuth2User customUser = (CustomOAuth2User) oAuth2User;
            return customUser.getOAuth2Response().getProviderId();
        } else if (oAuth2User != null) {
            // 일반 OAuth2User에서 providerId 추출 시도
            String providerId = oAuth2User.getAttribute("sub"); // Google의 경우 sub가 providerId
            if (providerId != null) {
                System.out.println("일반 OAuth2User에서 추출한 providerId: " + providerId);
                return providerId;
            }
        }
        return null;
    }

    @Operation(summary = "학교 인증 진행중 API", description = "아직 학교 인증이 진행중인 경우 리디렉션 경로")
    @GetMapping("/in-progress")
    private Response<InProgressResponse> inProgress(HttpServletRequest request){

        InProgressResponse inProgressResponse = memberService.inProgress(request);

        return Response.success(SuccessStatus.SUCCESS, inProgressResponse);

    }
}
