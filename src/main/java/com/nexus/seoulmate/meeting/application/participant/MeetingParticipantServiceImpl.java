package com.nexus.seoulmate.meeting.application.participant;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.meeting.api.dto.response.ParticipantsResDto;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingParticipantServiceImpl implements MeetingParticipantService{

    private final OrderRepository orderRepository;
    private final MeetingRepository meetingRepository;

    @Override
    public ParticipantsResDto getParticipantsByMeetingId(Long meetingId){
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        List<Member> paidMembers = orderRepository.findPaidMembersByMeetingId(meetingId);

        return ParticipantsResDto.from(meeting, paidMembers);
    }
}
