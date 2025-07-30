package com.nexus.seoulmate.meeting.application.privateMeeting;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;

public interface PrivateMeetingService {
    Response<Long> createMeeting(MeetingCreatePrivateReq req, Long userId);
    Response<MeetingDetailPrivateRes> getPrivateMeetingDetail(Long id, Long userId);
    Response<Long> updateMeeting(Long meetingId, MeetingUpdatePrivateReq req, Long userId);
    Response<Long> deleteMeeting(Long meetingId, Long userId);
}
