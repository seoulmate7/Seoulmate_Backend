package com.nexus.seoulmate.mypage;

import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.mypage.dto.MyPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Service
public class MyPageService {

    private final FluentProxyService fluentProxyService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public MyPageService(FluentProxyService fluentProxyService, MemberService memberService, MemberRepository memberRepository){
        this.fluentProxyService = fluentProxyService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    // 마이페이지 get
    public MyPageResponse getMyProfile(){
        Member member = memberService.getCurrentUser();

        return new MyPageResponse(
                member.getProfileImage(),
                member.getLastName() + " " + member.getFirstName(), // FIXME : 영어, 한국어 이름 어떻게 나누기로 했더라
                member.getEmail(),
                member.getBio(),
                member.getHobbies(),
                member.getUniv(),
                calculateAge(member.getDOB()),
                member.getLanguages()
        );
    }

    public int calculateAge(LocalDate DOB){
        LocalDate now = LocalDate.now();
        return Period.between(DOB, now).getYears();
    }

    // 프로필 사진 수정
    public void updateProfileImage(){
        Member member = memberService.getCurrentUser();
        // TODO: S3 업로드 로직 구현

        // 전에 있던 거 지우고 새로 등록
        // 전 프로필 사진은 S3에서도 지울 수 있나?
    }

    // 프로필 한 줄 소개 수정
    public void updateProfileBio(String bio){
        Member member = memberService.getCurrentUser();

        // 전에 있던 거 다 지우고 새로 등록
    }

    // 취미 수정
    public void updateHobbies(){
        Member member = memberService.getCurrentUser();

        // 전에 있던 거 다 지우고 새로 등록
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
}
