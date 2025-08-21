package com.nexus.seoulmate.mypage.dto;

import com.nexus.seoulmate.meeting.domain.MeetingType;

import java.time.LocalDate;
import java.time.LocalTime;

public record MeetingSimpleDto(
        Long meetingId,
        String image,
        String title,
        String location,
        LocalDate meetingDay,
        MeetingType meetingType,
        LocalTime startTime
) {
}
