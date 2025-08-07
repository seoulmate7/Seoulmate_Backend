package com.nexus.seoulmate.meeting.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.meeting.api.dto.response.ParticipantsResDto;
import com.nexus.seoulmate.meeting.application.participant.MeetingParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
@Tag(name = "MeetingParticipant", description = "모임 참가자 API")
public class MeetingParticipantController {

    private final MeetingParticipantService meetingParticipantService;

    @Operation(summary = "모임 참가자 조회", description = "결제 완료된 참가자 목록 조회")
    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<Response<ParticipantsResDto>> getParticipants(@PathVariable Long meetingId) {
        ParticipantsResDto resDto = meetingParticipantService.getParticipantsByMeetingId(meetingId);

        return ResponseEntity.ok(
                Response.success(SuccessStatus.GET_PARTICIPANTS, resDto)
        );
    }
}
