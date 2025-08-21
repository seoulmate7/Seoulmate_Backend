package com.nexus.seoulmate.mypage;

import com.nexus.seoulmate.aws.service.AmazonS3Service;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
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
import com.nexus.seoulmate.payment.application.PaymentService;
import com.nexus.seoulmate.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;

import com.nexus.seoulmate.mypage.dto.HobbyUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        switch (member.getCountry()) {
            case KOREA:
            case CHINA:
            case JAPAN:
                return member.getLastName() + member.getFirstName();
            default:
                return member.getFirstName() + " " + member.getLastName();
        }
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
    public void updateProfileBio(String newBio){
        Member member = memberService.getCurrentUser();

        // 전에 있던 거 다 지우고 새로 등록
        member.changeBio(newBio);

        memberRepository.save(member);
    }

    // 취미 수정
    public void updateHobbies(HobbyUpdateRequest dto){
        Member member = memberService.getCurrentUser();

        List<Hobby> newHobbies = new ArrayList<>();

        // DB에서 새로운 Hobby 엔티티 조회
        if(dto.getHobbies() != null && !dto.getHobbies().isEmpty()){
            for (String hobby : dto.getHobbies()){
                Hobby newHobby = hobbyRepository.findByHobbyName(hobby);
                if (newHobby != null) {
                    newHobbies.add(newHobby); 
                } else {
                    throw new IllegalArgumentException("존재하지 않는 취미 : " + hobby);
                }
            }
        }
        
        member.changeHobbies(newHobbies);
        memberRepository.save(member);
    }

    // 언어 레벨테스트 재응시
    // FluentProxyService 에서 진행
    public void updateLanguageLevel(MultipartFile audioFile, Languages language){
        Member member = memberService.getCurrentUser();
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
