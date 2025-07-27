package com.nexus.seoulmate.meeting.api.dto.request.privateReq;

import com.nexus.seoulmate.domain.member.domain.enums.Languages;

public record MeetingUpdatePrivateReq(
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
