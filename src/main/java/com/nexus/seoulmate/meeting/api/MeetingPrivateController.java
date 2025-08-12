package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingCreatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.request.privateReq.MeetingUpdatePrivateReq;
import com.nexus.seoulmate.meeting.api.dto.response.MeetingDetailPrivateRes;
import com.nexus.seoulmate.meeting.application.privateMeeting.PrivateMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetings/private")
@RequiredArgsConstructor
@Tag(name = "private 모임(사설)")
public class MeetingPrivateController {

    private final PrivateMeetingService privateMeetingService;

    @Operation(summary = "private 모임 생성 API")
    @PostMapping
    public ResponseEntity<Response<Long>> createMeeting(@RequestBody MeetingCreatePrivateReq req,
                                                  @RequestHeader("userId") Long userId){
        return ResponseEntity
                .status(SuccessStatus.CREATE_MEETING.getStatus())
                .body(privateMeetingService.createMeeting(req, userId));
    }

    @Operation(summary = "private 모임 상세 조회 API")
    @GetMapping("/{meetingId}")
    public ResponseEntity<Response<MeetingDetailPrivateRes>> getPrivateMeetingDetail(@PathVariable Long meetingId,
                                                                           @RequestHeader("userId") Long userId){
        return ResponseEntity.ok(privateMeetingService.getPrivateMeetingDetail(meetingId, userId));
    }

    @Operation(summary = "private 모임 수정 API")
    @PutMapping("/{meetingId}")
    public ResponseEntity<Response<Long>> updateMeeting(@PathVariable Long meetingId,
                                                    @RequestBody MeetingUpdatePrivateReq req,
                                                    @RequestHeader("userId") Long userId){
        return ResponseEntity.ok(privateMeetingService.updateMeeting(meetingId, req, userId));
    }

    @Operation(summary = "private 모임 삭제 API")
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Response<Long>> deleteMeeting(@PathVariable Long meetingId,
                                                    @RequestHeader("userId") Long userId){
        return ResponseEntity.ok(privateMeetingService.deleteMeeting(meetingId, userId));
    }

}
