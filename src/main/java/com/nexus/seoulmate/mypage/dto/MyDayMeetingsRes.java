package com.nexus.seoulmate.mypage.dto;

import java.time.LocalDate;
import java.util.List;

public record MyDayMeetingsRes(
        LocalDate meetingDay,
        List<MeetingSimpleDto> hosted,
        List<MeetingSimpleDto> participated
) {

    public static MyDayMeetingsRes of(LocalDate date,
                                      List<MeetingSimpleDto> hosted,
                                      List<MeetingSimpleDto> participated) {
        return new MyDayMeetingsRes(date, hosted, participated);
    }
}
