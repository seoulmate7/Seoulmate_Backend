package com.nexus.seoulmate.meeting.application.search;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingListRes;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingSearchServiceImpl implements MeetingSearchService{

    private final MeetingRepository meetingRepository;

    @Override
    public List<MeetingListRes> searchMeetings(MeetingSearchReq req){
        List<Meeting> meetings = meetingRepository.findBySearchCondition(req);

        if(meetings.isEmpty()){
            throw new CustomException(ErrorStatus.MEETING_NOT_FOUND);
        }

        return meetings.stream()
                .map(m -> new MeetingListRes(
                        m.getId(),
                        m.getMeetingType().name(),
                        m.getImage(),
                        m.getTitle(),
                        m.getMeetingDay().toString(),
                        m.getStartTime().toString(),
                        0 // 추후 궁합 계산 로직 삽입
                ))
                .toList();

    }
}
