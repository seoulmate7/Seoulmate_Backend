package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingCreateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;
import com.nexus.seoulmate.meeting.application.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final PrivateMeetingQueryService privateMeetingQueryService;
    private final OfficialMeetingQueryService officialMeetingQueryService;

    // 모임 생성 (official, private 구분 백엔드에서)
    @PostMapping
    public ResponseEntity<MeetingRes> createMeeting(@RequestBody MeetingCreateReq req,
                                                    @RequestHeader("userId") Long userId){
        MeetingRes res = meetingService.createMeeting(req, userId);
        return ResponseEntity.ok(res);
    }

    // official 모임 상세 조회
    @GetMapping("/official/{id}")
    public ResponseEntity<MeetingDetailOfficialRes> getOfficialMeetingDetail(@PathVariable Long id){
        return ResponseEntity.ok(officialMeetingQueryService.getOfficialMeetingDetail(id));
    }

    // private 모임 상세 조회
    @GetMapping("/private/{id}")
    public ResponseEntity<MeetingDetailPrivateRes> getPrivateMeetingDetail(@PathVariable Long id,
                                                                           @RequestHeader("userId") Long userId){
        return ResponseEntity.ok(privateMeetingQueryService.getPrivateMeetingDetail(id, userId));
    }
}
