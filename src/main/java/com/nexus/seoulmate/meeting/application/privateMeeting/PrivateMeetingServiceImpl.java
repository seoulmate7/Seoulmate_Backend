package com.nexus.seoulmate.meeting.application.privateMeeting;

import com.nexus.seoulmate.domain.member.domain.Member;
import com.nexus.seoulmate.domain.member.domain.enums.Role;
import com.nexus.seoulmate.domain.member.repository.MemberRepository;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
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
public class PrivateMeetingServiceImpl implements PrivateMeetingService {

    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MeetingRes createMeeting(MeetingCreatePrivateReq req, Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE); // ADMIN은 private 생성 불가
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        LocalDate meetingDay = LocalDate.parse(req.meeting_day(), formatter);
        LocalTime startTime = LocalTime.parse(req.start_time());

        Meeting meeting = Meeting.builder()
                .meetingType(MeetingType.PRIVATE)
                .meetingDay(meetingDay)
                .startTime(startTime)
                .location(req.location())
                .minParticipants(req.min_participants())
                .maxParticipants(req.max_participants())
                .currentParticipants(0)
                .price(req.price())
                .category(req.category())
                .image(req.image())
                .title(req.title())
                .hostMessage(req.host_message())
                .language(req.language())
                .languageLevel(null)
                .userId(member)
                .build();

        meetingRepository.save(meeting);
        return MeetingRes.from(meeting.getId(), SuccessStatus.CREATE_MEETING);
    }

    public MeetingDetailPrivateRes getPrivateMeetingDetail(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
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
    @Override
    @Transactional
    public MeetingRes updateMeeting(Long meetingId, MeetingUpdatePrivateReq req, Long userId) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if (!meeting.getUserId().getUserId().equals(userId)) {
            throw new CustomException(ErrorStatus.FORBIDDEN);
        }

        if (meeting.getMeetingType() != MeetingType.PRIVATE) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        LocalDate meetingDay = LocalDate.parse(req.meeting_day(), formatter);
        LocalTime startTime = LocalTime.parse(req.start_time());

        meeting.updatePrivateMeeting(
                req.title(),
                req.image(),
                req.location(),
                req.category(),
                meetingDay,
                startTime,
                req.min_participants(),
                req.max_participants(),
                req.language(),
                req.host_message(),
                req.price()
        );

        return MeetingRes.from(meeting.getId(), SuccessStatus.UPDATE_MEETING);
    }

    @Override
    @Transactional
    public MeetingRes deleteMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if (!meeting.getUserId().getUserId().equals(userId)) {
            throw new CustomException(ErrorStatus.FORBIDDEN);
        }

        if (meeting.getMeetingType() != MeetingType.PRIVATE) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        meetingRepository.delete(meeting);
        return MeetingRes.from(meeting.getId(), SuccessStatus.DELETE_MEETING);
    }
}
