package com.nexus.seoulmate.home.application;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.home.api.dto.response.CategoryMeetingCountRes;
import com.nexus.seoulmate.home.api.dto.response.HomeFeedRes;
import com.nexus.seoulmate.home.api.dto.response.MeetingBasicInfoRes;
import com.nexus.seoulmate.home.domain.repository.MeetingHomeRepository;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.University;
import com.nexus.seoulmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final MeetingHomeRepository meetingHomeRepository;

    public HomeFeedRes buildHome(Long userId) {
        Member m = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        // 학교
        University univ = m.getUniv();

        // 본인 학교 이름이 제목에 포함된 official 모임 1개
        Meeting regular = meetingHomeRepository.findOfficialByTitleContainsUniversity(univ);

        // 사용자 취미 카테고리와 같은 모임 5개
        List<Meeting> recommended = meetingHomeRepository.findRecommendedForUser(userId, 5);

        // 카테고리 집계
        var categoryCounts = meetingHomeRepository.countAllGroupByCategory().stream()
                .map(row -> new CategoryMeetingCountRes(
                        row.category(), row.category().getDisplayName(), row.count()
                )).toList();

        // 한국어 클래스
        List<Meeting> koreanClasses = meetingHomeRepository.findKoreanClasses(10);

        return new HomeFeedRes(
                toBasic(regular),
                recommended.stream().map(this::toBasic).toList(),
                categoryCounts,
                koreanClasses.stream().map(this::toBasic).toList()
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
