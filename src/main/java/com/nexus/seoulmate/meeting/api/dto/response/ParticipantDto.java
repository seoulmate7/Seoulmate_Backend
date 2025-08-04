package com.nexus.seoulmate.meeting.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "모임 참가자 응답 DTO")
public record ParticipantDto(
        @Schema(description = "회원 ID", example = "1")
        Long userId,

        @Schema(description = "이름", example = "정다운")
        String name
){
}
