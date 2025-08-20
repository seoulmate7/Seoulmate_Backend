package com.nexus.seoulmate.member.controller;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.domain.enums.*;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.dto.InProgressResponse;
import com.nexus.seoulmate.member.repository.GoogleInfoRepository;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.dto.signup.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.Optional;

import static com.nexus.seoulmate.exception.status.SuccessStatus.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Tag(name = "회원가입", description = "회원가입 관련 API")
public class MemberController {

    private final FluentProxyService fluentProxyService;
    private final MemberService memberService;
    private final GoogleInfoRepository googleInfoRepository;

    // 소셜 회원가입

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
        if (oAuth2User == null) {
            return Response.fail(ErrorStatus.UNAUTHORIZED);
        }

        // @AuthenticationPrincipal에서 googleId(sub)를 직접 가져오기
        String googleId = oAuth2User.getAttribute("sub");
        if (googleId == null) {
            return Response.fail(ErrorStatus.BAD_REQUEST);
        }

        System.out.println("추출된 googleId: " + googleId);

        // GoogleInfo 테이블에서 googleId로 정보 조회
        Optional<GoogleInfo> googleInfoOpt = googleInfoRepository.findByGoogleId(googleId);
        if (googleInfoOpt.isEmpty()) {
            return Response.fail(ErrorStatus.MEMBER_NOT_FOUND);
        }

        // DB에서 가져온 GoogleInfo 객체를 사용하여 정보 저장
        GoogleInfo googleInfo = googleInfoOpt.get();
        String profileImageUrl = memberService.uploadProfileImage(googleInfo.getGoogleId(), profileImage);

        Countries country;
        try {
            country = Countries.valueOf(countryStr);
        } catch (IllegalArgumentException e) {
            country = Countries.fromDisplayName(countryStr);
        }
        
        // ProfileCreateRequest 객체 생성
        ProfileCreateRequest profileCreateRequest = new ProfileCreateRequest(
                firstName, lastName, LocalDate.parse(DOB), country, bio, profileImageUrl
        );

        // memberService.saveProfile 메서드를 호출하여 프로필 저장
        memberService.saveProfile(profileCreateRequest, googleInfo.getGoogleId());
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
