package com.nexus.seoulmate.home.api.dto.response;

import java.util.List;

public record HomeFeedRes(
        MeetingBasicInfoRes regularMeeting,
        List<MeetingBasicInfoRes> recommendedMeetings,
        List<CategoryMeetingCountRes> schoolCategories,
        List<MeetingBasicInfoRes> koreanClasses
) {
}
