package com.nexus.seoulmate.meeting.domain.repository;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.domain.Meeting;

import java.util.List;

public interface MeetingCustomRepository {
    List<Meeting> findBySearchCondition(MeetingSearchReq req);
}
