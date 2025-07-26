package com.nexus.seoulmate.meeting.application;

import com.nexus.seoulmate.domain.member.domain.Member;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivateMeetingQueryServiceImpl implements PrivateMeetingQueryService{

    private final MeetingRepository meetingRepository;

    public MeetingDetailPrivateRes getPrivateMeetingDetail(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if(meeting.getMeetingType() != MeetingType.PRIVATE){
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        // 호스트 정보
        Member host = meeting.getUserId();
        // 궁합 정보
        int compatibilityScore = 85; // 추후 알고리즘 개발 후 수정

        return new MeetingDetailPrivateRes(
                meeting.getId(),
                meeting.getMeetingType().toString(),
                meeting.getImage(),
                meeting.getTitle(),
                new MeetingDetailPrivateRes.HostInfo(
                        host.getUserId(),
                        host.getFirstName() + " " + host.getLastName(),
                        host.getProfileImage()
                ),
                meeting.getLocation(),
                meeting.getMeetingDay().toString(),
                meeting.getStartTime().toString(),
                meeting.getMinParticipants(),
                meeting.getMaxParticipants(),
                meeting.getCurrentParticipants(),
                meeting.getLanguage().name(),
                meeting.getHostMessage(),
                meeting.getPrice(),
                compatibilityScore // 궁합 추후 수정
        );
    }
}
