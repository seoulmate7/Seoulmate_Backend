package com.nexus.seoulmate.home.api.dto.response;

public record MeetingBasicInfoRes(
        Long id,
        String type,
        String image,
        String title,
        String meeting_day,
        String start_time
) {
}
