package com.nexus.seoulmate.home.api.dto.response;

import com.nexus.seoulmate.member.domain.enums.HobbyCategory;

public record CategoryMeetingCountRes(
        HobbyCategory category,
        String displayName,
        long count
) {
}
