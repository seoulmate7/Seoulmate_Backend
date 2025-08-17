package com.nexus.seoulmate.mypage;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.mypage.dto.MyPageResponse;
import com.nexus.seoulmate.mypage.dto.HobbyUpdateRequest;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MyPageService {

    private final FluentProxyService fluentProxyService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final HobbyRepository hobbyRepository;

    public MyPageService(FluentProxyService fluentProxyService, MemberService memberService, MemberRepository memberRepository, HobbyRepository hobbyRepository){
        this.fluentProxyService = fluentProxyService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.hobbyRepository = hobbyRepository;
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
                member.calculateAge(),
                member.getLanguages()
        );
    }

    // 프로필 사진 수정
    public void updateProfileImage(MultipartFile profileImage){
        Member member = memberService.getCurrentUser();

        String profileImageUrl = "이미지 url"; // TODO: S3 업로드 로직 구현

        member.changeProfileImage(profileImageUrl);

        // 전에 있던 거 지우고 새로 등록
        // 전 프로필 사진은 S3에서도 지울 수 있나?
    }

    // 프로필 한 줄 소개 수정
    public void updateProfileBio(String newBio){
        Member member = memberService.getCurrentUser();

        // 전에 있던 거 다 지우고 새로 등록
        member.changeBio(newBio);
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
