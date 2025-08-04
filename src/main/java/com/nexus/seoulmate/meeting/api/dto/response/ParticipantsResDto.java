package com.nexus.seoulmate.meeting.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "모임 참가자 목록 응답")
public record ParticipantsResDto(
        @Schema(description = "모임 ID", example = "8")
        Long meetingId,

        @Schema(description = "모임 제목", example = "영어 프리토킹 모임")
        String meetingTitle,

        @Schema(description = "총 참여자 수", example = "5")
        int totalParticipants,

        @Schema(description = "참여자 목록")
        List<ParticipantDto> participants
){}
