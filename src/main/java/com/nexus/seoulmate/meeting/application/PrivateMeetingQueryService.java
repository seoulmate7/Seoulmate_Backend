package com.nexus.seoulmate.meeting.application;

import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;

public interface PrivateMeetingQueryService {
    MeetingDetailPrivateRes getPrivateMeetingDetail(Long id, Long userId);
}
