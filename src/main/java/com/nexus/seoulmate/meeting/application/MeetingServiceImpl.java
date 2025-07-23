package com.nexus.seoulmate.meeting.application;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingCreateReq;
import com.nexus.seoulmate.meeting.api.dto.request.MeetingUpdateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService{

    private final MeetingRepository meetingRepository;
//    private final UserRepository userRepository;

    @Override
    public MeetingRes createMeeting(MeetingCreateReq req, Long userId){

    }

    @Override
    public MeetingDetailRes getMeetingDetail(Long meetingId){

    }

    @Override
    public MeetingRes updateMeeting(Long meetingId, MeetingUpdateReq req, Long userId){

    }

    @Override
    public MeetingRes deleteMeeting(Long meetingId, Long userId){

    }
}
