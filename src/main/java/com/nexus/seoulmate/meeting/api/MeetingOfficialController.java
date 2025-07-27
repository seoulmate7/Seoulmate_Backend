package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingCreateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingUpdateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;
import com.nexus.seoulmate.meeting.application.officialMeeting.OfficialMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings/official")
@RequiredArgsConstructor
public class MeetingOfficialController {

    private final OfficialMeetingService officialMeetingService;

    @Operation(summary = "official 모임 생성 API")
    @PostMapping
    public ResponseEntity<MeetingRes> createMeeting(@RequestBody MeetingCreateOfficialReq req,
                                                    @RequestHeader("userId") Long userId){
        MeetingRes res = officialMeetingService.createMeeting(req, userId);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "official 모임 상세 조회 API")
    @GetMapping("/official/{id}")
    public ResponseEntity<MeetingDetailOfficialRes> getOfficialMeetingDetail(@PathVariable Long id){
        return ResponseEntity.ok(officialMeetingService.getOfficialMeetingDetail(id));
    }

    @Operation(summary = "official 모임 수정 API")
    @PutMapping("/{meetingId}")
    public ResponseEntity<MeetingRes> updateMeeting(@PathVariable Long meetingId,
                                                    @RequestBody MeetingUpdateOfficialReq req,
                                                    @RequestHeader("userId") Long userId){
        MeetingRes res = officialMeetingService.updateMeeting(meetingId, req, userId);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "official 모임 삭제 API")
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<MeetingRes> deleteMeeting(@PathVariable Long meetingId,
                                                    @RequestHeader("userId") Long userId){
        MeetingRes res = officialMeetingService.deleteMeeting(meetingId, userId);
        return ResponseEntity.ok(res);
    }
}
