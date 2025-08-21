package com.nexus.seoulmate.home.application;

import com.nexus.seoulmate.home.api.dto.response.CategoryMeetingCountRes;
import com.nexus.seoulmate.home.api.dto.response.HomeFeedRes;
import com.nexus.seoulmate.home.api.dto.response.MeetingBasicInfoRes;
import com.nexus.seoulmate.home.domain.repository.MeetingHomeRepository;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.University;
import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import com.nexus.seoulmate.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberService memberService;
    private final MeetingHomeRepository meetingHomeRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    public HomeFeedRes buildHome(HttpServletRequest request) {
        Member member = memberService.getCurrentUser();

        // 학교
        University univ = member.getUniv();

        // 본인 학교 이름이 제목에 포함된 official 모임 1개
        Meeting regular = meetingHomeRepository.findOfficialByTitleContainsUniversity(univ);

        // 사용자 취미 카테고리와 같은 모임 3개
        List<Meeting> recommended = meetingHomeRepository.findRecommendedForUser(member.getUserId(), 3);

        // 카테고리 집계
        var categoryCounts = meetingHomeRepository.countAllGroupByCategory().stream()
                .map(row -> new CategoryMeetingCountRes(
                        row.category(), row.category().getDisplayName(), row.count()
                )).toList();

        // 한국어 클래스 3개만
        List<Meeting> koreanClasses = meetingHomeRepository.findKoreanClasses(3);

            // JSESSIONID 쿠키 찾기
            customOAuth2UserService.changeJsessionId(request);
            String jsessionId = memberService.getSessionId(request);

        return new HomeFeedRes(
                toBasic(regular),
                recommended.stream().map(this::toBasic).toList(),
                categoryCounts,
                koreanClasses.stream().map(this::toBasic).toList(),
                member.getEmail(),
                member.getUserId(),
                member.getRole(),
                member.getUnivVerification(),
                "JSESSIONID=" + jsessionId
        );
    }

    private MeetingBasicInfoRes toBasic(Meeting regular) {
        if(regular == null) return null;
        return new MeetingBasicInfoRes(
                regular.getId(),
                regular.getMeetingType().name(),
                regular.getImage(),
                regular.getTitle(),
                regular.getMeetingDay().toString(),
                regular.getStartTime().toString()
        );
    }

    public List<MeetingBasicInfoRes> listByCategory(HobbyCategory category, int page, int size){
        return meetingHomeRepository.findByCategory(category, page, size)
                .stream()
                .map(this::toBasic)
                .toList();
    }
}
