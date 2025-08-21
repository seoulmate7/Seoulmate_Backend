package com.nexus.seoulmate.mypage.repository;

import com.nexus.seoulmate.mypage.dto.MeetingSimpleDto;

import java.time.LocalDate;
import java.util.List;

public interface MyMeetingQueryRepository {

    // 날짜별 주회 모임 조회
    List<MeetingSimpleDto> findHostedByUserAndDate(Long userId, LocalDate meetingDay);

    // 날짜별 참여 모임 조회 (official은 무조건 포함, private은 결제상태만)
    List<MeetingSimpleDto> findParticipatedConfirmedByUserAndDate(Long userId, LocalDate meetingDay);
}
