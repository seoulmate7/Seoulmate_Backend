package com.nexus.seoulmate.meeting.api.dto.response;

public record MeetingListRes(
        Long id,
        String type,
        String image,
        String title,
        String meeting_day,
        String start_time
) {
}
