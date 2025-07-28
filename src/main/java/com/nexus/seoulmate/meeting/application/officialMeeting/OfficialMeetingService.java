package com.nexus.seoulmate.meeting.application.officialMeeting;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingCreateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingUpdateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;

public interface OfficialMeetingService {
    Response<Long> createMeeting(MeetingCreateOfficialReq req, Long userId);
    Response<MeetingDetailOfficialRes> getOfficialMeetingDetail(Long id);
    Response<Long> updateMeeting(Long meetingId, MeetingUpdateOfficialReq req, Long userId);
    Response<Long> deleteMeeting(Long meetingId, Long userId);
}
