package com.nexus.seoulmate.member.service;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.dto.signup.*;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TempStorage tempStorage;
    private final MemberRepository memberRepository;
    private final HobbyRepository hobbyRepository;

    // 1. 프로필 생성
    public void saveProfile(ProfileCreateRequest profileCreateRequest, MultipartFile profileImage){
        String profileImageUrl = uploadProfileImage(profileCreateRequest.getGoogleId(), profileImage);

        tempStorage.save(profileCreateRequest, profileImageUrl);
    }

    // 1-1. 프로필 이미지 업로드
    public String uploadProfileImage(String googleId, MultipartFile profileImage) {
        // TODO: S3 업로드 로직 구현
        // 임시로 파일명 반환
        return "https://seoulmate-s3-bucket.s3.amazonaws.com/profile/" + googleId + "/" + profileImage.getOriginalFilename();
    }

    // 2. 언어 레벨 테스트
    // FluentProxyService 에서 진행
    public void submitLevelTest(LevelTestRequest levelTestRequest){

        tempStorage.save(levelTestRequest);
    }

    // 3. 취미 선택
    public void selectHobby(HobbyRequest hobbyRequest){
        tempStorage.save(hobbyRequest);
    }

    // 4. 학교 인증
    public void authUniv(UnivAuthRequest univAuthRequest){
        tempStorage.save(univAuthRequest);
    }

    // 1-1. 프로필 이미지 업로드
    public String uploadUnivCertificate(String googleId, MultipartFile profileImage) {
        // TODO: S3 업로드 로직 구현
        // 임시로 파일명 반환
        return "https://seoulmate-s3-bucket.s3.amazonaws.com/certificate/" + googleId + "/" + profileImage.getOriginalFilename();
    }

        // 정보 다 합쳐서 회원가입 완료 + 모든 회원간의 궁합 생성하기
    public void completeSignup(String googleId) {
        MemberCreateRequest request = tempStorage.collect(googleId);

        // 기존 Hobby 엔티티들을 조회
        List<Hobby> existingHobbies = new ArrayList<>();
        if (request.getHobbies() != null) {
            for (Hobby hobby : request.getHobbies()) {
                // hobbyName으로 기존 Hobby 엔티티 조회
                hobbyRepository.findByHobbyNameAndHobbyCategory(hobby.getHobbyName(), hobby.getHobbyCategory())
                        .ifPresent(existingHobbies::add);
            }
        }

        Member member = Member.createGoogleUser(
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getDOB(),
                request.getCountry(),
                request.getBio(),
                request.getProfileImage(),
                existingHobbies, // 기존 Hobby 엔티티들 사용
                request.getUnivCertificate(),
                request.getUniv(),
                request.getLanguages(),
                request.getVerificationStatus(),
                request.getAuthProvider()
        );

        memberRepository.save(member);

        // Todo : 모든 회원의 궁합 계산하기
    }

    // Todo : 현재 로그인한 사용자 정보 가져오기
    public Object getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth != null && auth.isAuthenticated() &&
                auth.getPrincipal() instanceof OAuth2User oAuth2User)) {
            return false;
        }

        String email = oAuth2User.getAttribute("email");
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.isPresent() ?
                "학교 인증 진행 상황 : " + optionalMember.get().getUnivVerification() :
                false;
    }
}
