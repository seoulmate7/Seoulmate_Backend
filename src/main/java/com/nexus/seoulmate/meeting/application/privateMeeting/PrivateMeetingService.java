package com.nexus.seoulmate.meeting.application.privateMeeting;

import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;

public interface PrivateMeetingService {
    MeetingRes createMeeting(MeetingCreatePrivateReq req, Long userId);
    MeetingDetailPrivateRes getPrivateMeetingDetail(Long id, Long userId);
    MeetingRes updateMeeting(Long meetingId, MeetingUpdatePrivateReq req, Long userId);
    MeetingRes deleteMeeting(Long meetingId, Long userId);
}
