package com.nexus.seoulmate.meeting.api.dto.response;

import com.nexus.seoulmate.member.domain.enums.HobbyCategory;

public record MeetingDetailPrivateRes(
        Long id,
        String type,
        String image,
        String title,
        HostInfo host,
        String location,
        HobbyCategory hobbyCategory,
        String primaryHobbyName,
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
            String profile_image,
            int language_level
    ){}
}
