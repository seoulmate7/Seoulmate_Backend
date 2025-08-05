package com.nexus.seoulmate.mypage;

import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.service.FluentProxyService;
import com.nexus.seoulmate.mypage.dto.MyPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MyPageService {

    private final FluentProxyService fluentProxyService;

    public MyPageService(FluentProxyService fluentProxyService){
        this.fluentProxyService = fluentProxyService;
    }

    // 마이페이지 get
    public MyPageResponse getMyProfile(){
        // Todo : getCurrentMember

        return new MyPageResponse();
    }

    // 프로필 사진 수정
    public void updateProfileImage(){
        // Todo : getCurrentMember

        // 전에 있던 거 지우고 새로 등록
        // 전 프로필 사진은 S3에서도 지울 수 있나?
    }

    // 프로필 한 줄 소개 수정
    public void updateProfileBio(String bio){
        // Todo : getCurrentMember

        // 전에 있던 거 다 지우고 새로 등록
    }

    // 취미 수정
    public void updateHobbies(){
        // Todo : getCurrentMember

        // 전에 있던 거 다 지우고 새로 등록
    }

    // 언어 레벨테스트 재응시
    public void updateLanguageLevel(MultipartFile audioFile, Languages language){
        // Todo : getCurrentMember
        String languageLevel = fluentProxyService.fluentFlow(audioFile, language);

        // 해당 언어가 있으면 대체, 없으면 새로 등록
    }
}
