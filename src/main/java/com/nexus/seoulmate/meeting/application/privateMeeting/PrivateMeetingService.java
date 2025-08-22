package com.nexus.seoulmate.meeting.application.privateMeeting;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;

public interface PrivateMeetingService {
    Response<Long> createMeeting(MeetingCreatePrivateReq req);
    Response<MeetingDetailPrivateRes> getPrivateMeetingDetail(Long id);
    Response<Long> updateMeeting(Long meetingId, MeetingUpdatePrivateReq req);
    Response<Long> deleteMeeting(Long meetingId);
}
