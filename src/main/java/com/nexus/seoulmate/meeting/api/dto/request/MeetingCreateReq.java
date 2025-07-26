package com.nexus.seoulmate.meeting.api.dto.request;

import com.nexus.seoulmate.domain.member.domain.enums.Languages;

public record MeetingCreateReq(
        String meeting_day,
        String start_time,
        String location,
        int min_participants,
        int max_participants,
        int price,
        String category,
        String image,
        String title,
        String host_message,
        Languages language
) {
}
