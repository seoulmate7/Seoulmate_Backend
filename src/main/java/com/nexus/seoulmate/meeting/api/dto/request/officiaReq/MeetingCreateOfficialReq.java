package com.nexus.seoulmate.meeting.api.dto.request.officiaReq;

public record MeetingCreateOfficialReq(
        String title,
        String image,
        String location,
        String category,
        String meeting_day,
        String start_time,
        Integer max_participants,
        String host_message,
        Integer price
) {
}
