package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingCreateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.request.officiaReq.MeetingUpdateOfficialReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailOfficialRes;
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
    public ResponseEntity<Response<Long>> createMeeting(@RequestBody MeetingCreateOfficialReq req,
                                                    @RequestHeader("userId") Long userId){
        return ResponseEntity
                .status(SuccessStatus.CREATE_MEETING.getStatus())
                .body(officialMeetingService.createMeeting(req, userId));
    }

    @Operation(summary = "official 모임 상세 조회 API")
    @GetMapping("/official/{id}")
    public ResponseEntity<Response<MeetingDetailOfficialRes>> getOfficialMeetingDetail(@PathVariable Long id){
        return ResponseEntity.ok(officialMeetingService.getOfficialMeetingDetail(id));
    }

    @Operation(summary = "official 모임 수정 API")
    @PutMapping("/{meetingId}")
    public ResponseEntity<Response<Long>> updateMeeting(@PathVariable Long meetingId,
                                                    @RequestBody MeetingUpdateOfficialReq req,
                                                    @RequestHeader("userId") Long userId){
        return ResponseEntity.ok(officialMeetingService.updateMeeting(meetingId, req, userId));
    }

    @Operation(summary = "official 모임 삭제 API")
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Response<Long>> deleteMeeting(@PathVariable Long meetingId,
                                                    @RequestHeader("userId") Long userId){
        return ResponseEntity.ok(officialMeetingService.deleteMeeting(meetingId, userId));
    }
}
