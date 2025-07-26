package com.nexus.seoulmate.meeting.application;

import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;

public interface OfficialMeetingQueryService {
    MeetingDetailOfficialRes getOfficialMeetingDetail(Long id);
}
