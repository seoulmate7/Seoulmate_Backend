package com.nexus.seoulmate.meeting.application.search;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingListRes;

import java.util.List;

public interface MeetingSearchService {
    List<MeetingListRes> searchMeetings(MeetingSearchReq req);
}
