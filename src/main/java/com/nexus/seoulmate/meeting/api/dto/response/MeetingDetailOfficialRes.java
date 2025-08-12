package com.nexus.seoulmate.meeting.api.dto.response;

public record MeetingDetailOfficialRes(
        Long id,
        String type,
        String image,
        String title,
        String location,
        String meeting_day,
        String start_time,
        int max_participants,
        int current_participants,
        String host_message,
        int price
) {
}
