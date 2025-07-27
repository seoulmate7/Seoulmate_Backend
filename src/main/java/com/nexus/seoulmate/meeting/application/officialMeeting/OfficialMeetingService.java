package com.nexus.seoulmate.meeting.application.officialMeeting;

import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingCreateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingUpdateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;

public interface OfficialMeetingService {
    MeetingRes createMeeting(MeetingCreateOfficialReq req, Long userId);
    MeetingDetailOfficialRes getOfficialMeetingDetail(Long id);
    MeetingRes updateMeeting(Long meetingId, MeetingUpdateOfficialReq req, Long userId);
    MeetingRes deleteMeeting(Long meetingId, Long userId);
}
