package com.nexus.seoulmate.meeting.application;

import com.nexus.seoulmate.domain.member.domain.Member;
import com.nexus.seoulmate.domain.member.domain.enums.Role;
import com.nexus.seoulmate.domain.member.repository.MemberRepository;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.meeting.api.dto.request.MeetingCreateReq;
import com.nexus.seoulmate.meeting.api.dto.request.MeetingUpdateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService{

    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MeetingRes createMeeting(MeetingCreateReq req, Long userId){
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        MeetingType type;
        // 사용자 권한에 따라 모임의 type 설정
        if(member.getRole() == Role.ADMIN){
            type = MeetingType.OFFICIAL;
        } else {
            type = MeetingType.PRIVATE;
        }

        // 날짜, 시간 포맷 지정
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        LocalDate meetingDay = LocalDate.parse(req.meeting_day(), dateTimeFormatter);

        Meeting meeting = Meeting.builder()
                .meetingType(type)
                .meetingDay(meetingDay)
                .startTime(LocalTime.parse((req.start_time())))
                .location(req.location())
                .minParticipants(req.min_participants())
                .maxParticipants(req.max_participants())
                .currentParticipants(0) // 기본값 0
                .price(req.price())
                .category(req.category())
                .image(req.image())
                .title(req.title())
                .hostMessage(req.host_message())
                .language(req.language())
                .languageLevel(null) // 추후 연결
                .userId(member)
                .build();

        meetingRepository.save(meeting);
        return new MeetingRes(meeting.getId(), "모임이 성공적으로 생성되었습니다.");
    }

    @Override
    public MeetingRes updateMeeting(Long meetingId, MeetingUpdateReq req, Long userId){
        return null; // 수정
    }

    @Override
    public MeetingRes deleteMeeting(Long meetingId, Long userId){
        return null; // 수정
    }
}
