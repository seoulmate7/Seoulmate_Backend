package com.nexus.seoulmate.meeting.api.dto.response;

import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.domain.Meeting;

public record MeetingRes(
        Long meetingId,
        String message
) {
    public static MeetingRes from(Long meetingId, SuccessStatus status) {
        return new MeetingRes(meetingId, status.getMessage());
    }
}
