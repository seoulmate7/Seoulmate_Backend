package com.nexus.seoulmate.meeting.api.dto.request;

import com.nexus.seoulmate.domain.member.domain.enums.Languages;

public record MeetingUpdateReq(
        String title,
        String image,
        String location,
        String category,
        String meeting_day,
        String start_time,
        int min_participants,
        int max_participants,
        Languages language,
        String host_message,
        int price
        ) {
}
