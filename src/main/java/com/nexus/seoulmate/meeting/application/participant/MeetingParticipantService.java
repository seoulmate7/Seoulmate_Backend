package com.nexus.seoulmate.meeting.application.participant;

import com.nexus.seoulmate.meeting.api.dto.response.ParticipantsResDto;

public interface MeetingParticipantService {
    ParticipantsResDto getParticipantsByMeetingId(Long meetingId);
}
