package com.nexus.seoulmate.meeting.application.privateMeeting;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.domain.enums.Role;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PrivateMeetingServiceImpl implements PrivateMeetingService {

    private final MeetingRepository meetingRepository;
    private final HobbyRepository hobbyRepository;
    private final MemberService memberService;

    // 숫자 문자열 검증
    private static final Pattern NUMERIC = Pattern.compile("^\\d+$");
    private final MemberRepository memberRepository;

    private int parsePositiveInt(String raw, String fieldName){
        if(raw == null){
            throw new CustomException(ErrorStatus.INVALID_PARAMETER, fieldName + " 값이 입력되지 않았습니다.");
        }
        String val = raw.trim();
        if(!NUMERIC.matcher(val).matches()){
            throw new CustomException(ErrorStatus.INVALID_PARAMETER, fieldName + " 값은 숫자여야 합니다.");
        }
        try{
            return Integer.parseInt(val);
        } catch (NumberFormatException e){
            throw new CustomException(ErrorStatus.INVALID_PARAMETER, fieldName + " 값이 숫자 범위를 벗어났습니다.");
        }
    }

    private void validateParticipants(int min, int max) {
        if (min < 1) {
            throw new CustomException(ErrorStatus.INVALID_PARAMETER, "소 인원(min_participants)은 1명 이상이어야 합니다.");
        }
        if (max < 1) {
            throw new CustomException(ErrorStatus.INVALID_PARAMETER, "최대 인원(max_participants)은 1명 이상이어야 합니다.");
        }
        if (min > max) {
            throw new CustomException(ErrorStatus.INVALID_PARAMETER, "최소 인원(min_participants)은 최대 인원(max_participants)보다 클 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public Response<Long> createMeeting(MeetingCreatePrivateReq req) {
        Long userId = memberService.getCurrentId();
        Member member = memberRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

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
            hostLangLevel = member.getLanguages().get(meetingLang);
        }

        // string -> int 변환 및 검증
        int min = parsePositiveInt(req.min_participants(), "min_participants");
        int max = parsePositiveInt(req.max_participants(), "max_participants");
        validateParticipants(min, max);
        int price = parsePositiveInt(req.price(), "price"); // 0 허용

        Meeting meeting = Meeting.builder()
                .meetingType(MeetingType.PRIVATE)
                .meetingDay(meetingDay)
                .startTime(startTime)
                .location(req.location())
                .minParticipants(min)
                .maxParticipants(max)
                .currentParticipants(0)
                .price(price)
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
    public Response<MeetingDetailPrivateRes> getPrivateMeetingDetail(Long meetingId) {
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
                        host.getProfileImage(),
                        meeting.getLanguageLevel() == null ? 0 : meeting.getLanguageLevel()
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
    public Response<Long> updateMeeting(Long meetingId, MeetingUpdatePrivateReq req) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        Long meId = memberService.getCurrentId();
        if (!meeting.getUserId().getUserId().equals(meId)) {
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
            hostLangLevel = meeting.getUserId().getLanguages().get(meetingLang);
        }

        // string -> int 변환 및 검증
        int min = parsePositiveInt(req.min_participants(), "min_participants");
        int max = parsePositiveInt(req.max_participants(), "max_participants");
        validateParticipants(min, max);
        int price = parsePositiveInt(req.price(), "price");

        meeting.updatePrivateMeeting(
                req.title(),
                req.image(),
                req.location(),
                hobbyCategory,
                meetingDay,
                startTime,
                min,
                max,
                meetingLang,
                req.host_message(),
                price
        );

        meeting.updatePrimaryHobby(primaryHobby);
        // 언어 레벨도 갱신
        meeting.updateLanguageLevel(hostLangLevel);

        return Response.success(SuccessStatus.UPDATE_MEETING, meeting.getId());
    }

    @Override
    @Transactional
    public Response<Long> deleteMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findWithUserById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        Long meId = memberService.getCurrentId();
        if (!meeting.getUserId().getUserId().equals(meId)) {
            throw new CustomException(ErrorStatus.FORBIDDEN);
        }

        if (meeting.getMeetingType() != MeetingType.PRIVATE) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        meetingRepository.delete(meeting);
        return Response.success(SuccessStatus.DELETE_MEETING, meeting.getId());
    }
}
