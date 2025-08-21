package com.nexus.seoulmate.meeting.application.privateMeeting;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.domain.enums.Role;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.global.status.ErrorStatus;
import com.nexus.seoulmate.global.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
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
    private final HobbyRepository hobbyRepository;

    @Override
    @Transactional
    public Response<Long> createMeeting(MeetingCreatePrivateReq req, Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE); // ADMIN은 private 생성 불가
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        LocalDate meetingDay = LocalDate.parse(req.meeting_day(), formatter);
        LocalTime startTime = LocalTime.parse(req.start_time());


        // 세부 취미 조회
        String raw = req.primaryHobbyName();
        if(raw == null || raw.isBlank()) {
            throw new CustomException(ErrorStatus.HOBBY_NOT_FOUND);
        }
        String name = raw.trim();

        Hobby primaryHobby = hobbyRepository.findByHobbyNameIgnoreCase(name)
                .orElseThrow(() -> new CustomException(ErrorStatus.HOBBY_NOT_FOUND));

        HobbyCategory hobbyCategory = primaryHobby.getHobbyCategory();

        // 언어와 호스트 언어 레벨 연동
        Languages meetingLang = req.language(); // null값 허용하지 않음
        Integer hostLangLevel = null;
        if(meetingLang != null && member.getLanguages() != null){
            hostLangLevel = member.getLanguages().get(meetingLang.name());
        }

        Meeting meeting = Meeting.builder()
                .meetingType(MeetingType.PRIVATE)
                .meetingDay(meetingDay)
                .startTime(startTime)
                .location(req.location())
                .minParticipants(req.min_participants())
                .maxParticipants(req.max_participants())
                .currentParticipants(0)
                .price(req.price())
                .hobbyCategory(hobbyCategory) // 자동 세팅
                .primaryHobby(primaryHobby) // 세부 취미 연결
                .image(req.image())
                .title(req.title())
                .hostMessage(req.host_message())
                .language(meetingLang)
                .languageLevel(hostLangLevel) // 호스트의 해당 언어 레벨
                .userId(member)
                .build();

        meetingRepository.save(meeting);
        return Response.success(SuccessStatus.CREATE_MEETING, meeting.getId());
    }

    @Transactional(readOnly = true)
    public Response<MeetingDetailPrivateRes> getPrivateMeetingDetail(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if(meeting.getMeetingType() != MeetingType.PRIVATE){
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        // 호스트 정보
        Member host = meeting.getUserId();

        MeetingDetailPrivateRes dto = new MeetingDetailPrivateRes(
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
                meeting.getHobbyCategory(),
                meeting.getPrimaryHobby().getHobbyName(),
                meeting.getMeetingDay().toString(),
                meeting.getStartTime().toString(),
                meeting.getMinParticipants(),
                meeting.getMaxParticipants(),
                meeting.getCurrentParticipants(),
                meeting.getLanguage().name(),
                meeting.getHostMessage(),
                meeting.getPrice()
        );
        return Response.success(SuccessStatus.READ_MEETING_DETAIL, dto);
    }
    @Override
    @Transactional
    public Response<Long> updateMeeting(Long meetingId, MeetingUpdatePrivateReq req, Long userId) {
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

        // 세부 취미 재조회
        String raw = req.primaryHobbyName();
        if (raw == null || raw.isBlank()) {
            throw new CustomException(ErrorStatus.HOBBY_NOT_FOUND);
        }
        String name = raw.trim();

        Hobby primaryHobby = hobbyRepository.findByHobbyNameIgnoreCase(name)
                .orElseThrow(() -> new CustomException(ErrorStatus.HOBBY_NOT_FOUND));
        HobbyCategory hobbyCategory = primaryHobby.getHobbyCategory();

        // 언어와 언어 레벨 갱신
        Languages meetingLang = req.language();
        Integer hostLangLevel = null;
        if(meetingLang != null && meeting.getUserId().getLanguages() != null) {
            hostLangLevel = meeting.getUserId().getLanguages().get(meetingLang.name());
        }

        meeting.updatePrivateMeeting(
                req.title(),
                req.image(),
                req.location(),
                hobbyCategory,
                meetingDay,
                startTime,
                req.min_participants(),
                req.max_participants(),
                meetingLang,
                req.host_message(),
                req.price()
        );

        meeting.updatePrimaryHobby(primaryHobby);
        // 언어 레벨도 갱신
        meeting.updateLanguageLevel(hostLangLevel);

        return Response.success(SuccessStatus.UPDATE_MEETING, meeting.getId());
    }

    @Override
    @Transactional
    public Response<Long> deleteMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        if (!meeting.getUserId().getUserId().equals(userId)) {
            throw new CustomException(ErrorStatus.FORBIDDEN);
        }

        if (meeting.getMeetingType() != MeetingType.PRIVATE) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        meetingRepository.delete(meeting);
        return Response.success(SuccessStatus.DELETE_MEETING, meeting.getId());
    }
}
