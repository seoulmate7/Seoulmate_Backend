package com.nexus.seoulmate.home.api.dto.response;

import com.nexus.seoulmate.member.domain.enums.Role;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;

import java.util.List;

public record HomeFeedRes(
        MeetingBasicInfoRes regularMeeting,
        List<MeetingBasicInfoRes> recommendedMeetings,
        List<CategoryMeetingCountRes> schoolCategories,
        List<MeetingBasicInfoRes> koreanClasses,

        String email,
        Long userId,
        Role role,
        VerificationStatus univVerification,
        String jsessionId
) {
}
