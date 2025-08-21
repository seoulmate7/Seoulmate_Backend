package com.nexus.seoulmate.mypage;

import com.nexus.seoulmate.aws.service.AmazonS3Service;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.mypage.dto.MeetingSimpleDto;
import com.nexus.seoulmate.mypage.dto.MyPageResponse;

import com.nexus.seoulmate.mypage.repository.MyMeetingQueryRepository;
import lombok.RequiredArgsConstructor;

import com.nexus.seoulmate.mypage.dto.HobbyUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;

import static com.nexus.seoulmate.exception.status.ErrorStatus.HOBBY_SAVE_FAILED;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final FluentProxyService fluentProxyService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final HobbyRepository hobbyRepository;
    private final AmazonS3Service amazonS3Service;
    private final MyMeetingQueryRepository myMeetingQueryRepository;

    // 마이페이지 get
    @Transactional(readOnly = true)
    public MyPageResponse getMyProfile(){
        Member member = memberService.getCurrentUser();

        return new MyPageResponse(
                member.getProfileImage(),
                formatName(member),
                member.getEmail(),
                member.getBio(),
                member.getHobbies(),
                member.getUniv(),
                member.calculateAge(),
                member.getLanguages()
        );
    }

    private String formatName(Member member) {
        return switch (member.getCountry()) {
            case KOREA, CHINA, JAPAN -> member.getLastName() + member.getFirstName();
            default -> member.getFirstName() + " " + member.getLastName();
        };
    }

    // 프로필 사진 수정
    public void updateProfileImage(MultipartFile profileImage){
        Member member = memberService.getCurrentUser();

        String profileImageUrl = amazonS3Service.uploadProfile(profileImage).getUrl();

        member.changeProfileImage(profileImageUrl);

        memberRepository.save(member);

        // 전에 있던 거 지우고 새로 등록
        // 전 프로필 사진은 S3에서도 지울 수 있나?
    }

    // 프로필 한 줄 소개 수정
    @Transactional
    public void updateProfileBio(String newBio){
        Member member = memberService.getCurrentUser();

        // 전에 있던 거 다 지우고 새로 등록
        member.changeBio(newBio);

        memberRepository.save(member);
    }

    // 취미 수정
    @Transactional
    public void updateHobbies(HobbyUpdateRequest dto){
        Member member = memberService.getCurrentUser();

        List<Hobby> newHobbies = hobbyRepository.findByHobbyNameIn(dto.getHobbies());

        if (newHobbies.size() != dto.getHobbies().size()){
            throw new CustomException(HOBBY_SAVE_FAILED); // 존재하지 않는 취미가 포함되어 있습니다. 
        }
        
        member.changeHobbies(newHobbies);
        memberRepository.save(member);
    }

    // 언어 레벨테스트 재응시
    // FluentProxyService 에서 진행
    public void updateLanguageLevel(MultipartFile audioFile, String languageStr){
        Member member = memberService.getCurrentUser();

        // String을 Languages enum으로 변환 (한글 표시명 또는 enum 상수명 모두 지원)
        Languages language;
        try {
            // 먼저 enum 상수명으로 시도
            language = Languages.valueOf(languageStr);
        } catch (IllegalArgumentException e) {
            // enum 상수명이 아니면 한글 표시명으로 시도
            language = Languages.fromDisplayName(languageStr);
        }
        String languageLevel = fluentProxyService.fluentFlow(audioFile, language);
        int intLanguageLevel = (int) Double.parseDouble(languageLevel);

        // 해당 언어의 평가 결과가 이미 있으면 대체, 없으면 새로 등록 -> Map 은 어차피 중복 Key 불가능해서 그냥 put해도 중복 불가능
        Map<Languages, Integer> languages = member.getLanguages();
        languages.put(language, intLanguageLevel);

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<MeetingSimpleDto> getMyHostedMeetingsByDate(LocalDate date) {
        Member member = memberService.getCurrentUser();
        return myMeetingQueryRepository.findHostedByUserAndDate(member.getUserId(), date);
    }

    @Transactional(readOnly = true)
    public List<MeetingSimpleDto> getMyParticipatedMeetingsByDate(LocalDate date) {
        Member member = memberService.getCurrentUser();
        return myMeetingQueryRepository.findParticipatedConfirmedByUserAndDate(member.getUserId(), date);
    }
}
