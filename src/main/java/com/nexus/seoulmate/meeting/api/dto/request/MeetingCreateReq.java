package com.nexus.seoulmate.meeting.api.dto.request;

import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.meeting.domain.Language;

public record MeetingCreateReq(
        MeetingType type, // personal or official
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
        Language language
) {
}
