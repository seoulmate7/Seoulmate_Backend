package com.nexus.seoulmate.meeting.application;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingCreateReq;
import com.nexus.seoulmate.meeting.api.dto.request.MeetingUpdateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;

public interface MeetingService {
    MeetingRes createMeeting(MeetingCreateReq req, Long userId);
    MeetingRes updateMeeting(Long meetingId, MeetingUpdateReq req, Long userId);
    MeetingRes deleteMeeting(Long meetingId, Long userId);
}
