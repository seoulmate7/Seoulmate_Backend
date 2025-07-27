package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingRes;
import com.nexus.seoulmate.meeting.application.privateMeeting.PrivateMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings/private")
@RequiredArgsConstructor
public class MeetingPrivateController {

    private final PrivateMeetingService privateMeetingService;

    @Operation(summary = "private 모임 생성 API")
    @PostMapping
    public ResponseEntity<MeetingRes> createMeeting(@RequestBody MeetingCreatePrivateReq req,
                                                    @RequestHeader("userId") Long userId){
        MeetingRes res = privateMeetingService.createMeeting(req, userId);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "private 모임 상세 조회 API")
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDetailPrivateRes> getPrivateMeetingDetail(@PathVariable Long meetingId,
                                                                           @RequestHeader("userId") Long userId){
        return ResponseEntity.ok(privateMeetingService.getPrivateMeetingDetail(meetingId, userId));
    }

    @Operation(summary = "private 모임 수정 API")
    @PutMapping("/{meetingId}")
    public ResponseEntity<MeetingRes> updateMeeting(@PathVariable Long meetingId,
                                                    @RequestBody MeetingUpdatePrivateReq req,
                                                    @RequestHeader("userId") Long userId){
        MeetingRes res = privateMeetingService.updateMeeting(meetingId, req, userId);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "private 모임 삭제 API")
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<MeetingRes> deleteMeeting(@PathVariable Long meetingId,
                                                    @RequestHeader("userId") Long userId){
        MeetingRes res = privateMeetingService.deleteMeeting(meetingId, userId);
        return ResponseEntity.ok(res);
    }

}
