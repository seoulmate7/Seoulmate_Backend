package com.nexus.seoulmate.meeting.application;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfficialMeetingQueryServiceImpl implements OfficialMeetingQueryService{

    private final MeetingRepository meetingRepository;

    public MeetingDetailOfficialRes getOfficialMeetingDetail(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if(meeting.getMeetingType() != MeetingType.OFFICIAL){
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        int compatibilityScore = 85; // 추후 알고리즘 개발 후 수정

        return new MeetingDetailOfficialRes(
                meeting.getId(),
                meeting.getMeetingType().name(),
                meeting.getImage(),
                meeting.getTitle(),
                meeting.getLocation(),
                meeting.getMeetingDay().toString(),
                meeting.getStartTime().toString(),
                meeting.getMaxParticipants(),
                meeting.getCurrentParticipants(),
                meeting.getHostMessage(),
                meeting.getPrice(),
                compatibilityScore
        );
    }
}
