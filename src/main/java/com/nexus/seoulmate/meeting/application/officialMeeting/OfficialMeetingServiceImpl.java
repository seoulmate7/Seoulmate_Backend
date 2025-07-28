package com.nexus.seoulmate.meeting.application.officialMeeting;

import com.nexus.seoulmate.domain.member.domain.Member;
import com.nexus.seoulmate.domain.member.domain.enums.Role;
import com.nexus.seoulmate.domain.member.repository.MemberRepository;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingCreateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingUpdateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;
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
public class OfficialMeetingServiceImpl implements OfficialMeetingService {

    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Response<Long> createMeeting(MeetingCreateOfficialReq req, Long userId){
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        if (member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorStatus.FORBIDDEN);
        }

        // 날짜, 시간 포맷 지정
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        LocalDate meetingDay = LocalDate.parse(req.meeting_day(), dateTimeFormatter);
        LocalTime startTime = LocalTime.parse(req.start_time());

        Meeting meeting = Meeting.builder()
                .meetingType(MeetingType.OFFICIAL)
                .meetingDay(meetingDay)
                .startTime(startTime)
                .location(req.location())
                .maxParticipants(req.max_participants())
                .currentParticipants(0) // 기본값 0
                .price(req.price())
                .category(req.category())
                .image(req.image())
                .title(req.title())
                .hostMessage(req.host_message())
                .language(null) // official은 언어 제한 없음
                .languageLevel(null) // 추후 연결
                .userId(member)
                .build();

        meetingRepository.save(meeting);
        return Response.success(SuccessStatus.CREATE_MEETING, meeting.getId());
    }

    public Response<MeetingDetailOfficialRes> getOfficialMeetingDetail(Long meetingId) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if(meeting.getMeetingType() != MeetingType.OFFICIAL){
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        int compatibilityScore = 85; // 추후 알고리즘 개발 후 수정

        MeetingDetailOfficialRes dto = new MeetingDetailOfficialRes(
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
        return Response.success(SuccessStatus.READ_MEETING_DETAIL, dto);
    }

    @Override
    @Transactional
    public Response<Long> updateMeeting(Long meetingId, MeetingUpdateOfficialReq req, Long userId){
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        // 작성자 확인
        if(!meeting.getUserId().getUserId().equals(userId)){
            throw new CustomException(ErrorStatus.FORBIDDEN);
        }

        if (meeting.getMeetingType() != MeetingType.OFFICIAL) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        LocalDate meetingDay = LocalDate.parse(req.meeting_day(), dateTimeFormatter);
        LocalTime startTime = LocalTime.parse(req.start_time());

            meeting.updateOfficialMeeting(
                    req.title(),
                    req.image(),
                    req.location(),
                    req.category(),
                    meetingDay,
                    startTime,
                    req.max_participants(),
                    req.host_message(),
                    req.price()
            );

        return Response.success(SuccessStatus.UPDATE_MEETING, meeting.getId());
    }

    @Override
    @Transactional
    public Response<Long> deleteMeeting(Long meetingId, Long userId){
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if(!meeting.getUserId().getUserId().equals(userId)){
            throw new CustomException(ErrorStatus.FORBIDDEN);
        }

        if (meeting.getMeetingType() != MeetingType.OFFICIAL) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        meetingRepository.delete(meeting);

        return Response.success(SuccessStatus.DELETE_MEETING, meeting.getId());
    }
}
