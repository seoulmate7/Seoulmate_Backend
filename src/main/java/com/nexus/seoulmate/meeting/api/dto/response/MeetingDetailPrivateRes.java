package com.nexus.seoulmate.meeting.api.dto.response;

public record MeetingDetailPrivateRes(
        Long id,
        String type,
        String image,
        String title,
        HostInfo host,
        String location,
        String meeting_day,
        String start_time,
        int min_participants,
        int max_participants,
        int current_participants,
        String language,
        String host_message,
        int price
) {
    public record HostInfo(
            Long id,
            String name,
            String profile_image
    ){}
}
