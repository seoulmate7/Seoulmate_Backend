package com.nexus.seoulmate.meeting.api.dto.request.privateReq;

import com.nexus.seoulmate.member.domain.enums.Languages;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개인 모임 수정 요청 DTO")
public record MeetingUpdatePrivateReq(

        @Schema(description = "모임 제목", example = "한강에서 프랑스어 모임 - 수정")
        String title,

        @Schema(description = "대표 이미지 URL", example = "https://example.com/image-updated.png")
        String image,

        @Schema(description = "장소", example = "여의도 한강공원 A구역")
        String location,

        @Schema(description = "카테고리", example = "언어교환")
        String category,

        @Schema(description = "모임 날짜 (dd/MM/yyyy)", example = "29/07/2025")
        String meeting_day,

        @Schema(description = "시작 시간 (HH:mm)", example = "19:00")
        String start_time,

        @Schema(description = "최소 인원", example = "3")
        int min_participants,

        @Schema(description = "최대 인원", example = "8")
        int max_participants,

        @Schema(description = "사용 언어", example = "FRENCH")
        Languages language,

        @Schema(description = "호스트 메시지", example = "즐겁게 대화해요 :)")
        String host_message,

        @Schema(description = "참가비", example = "7000")
        int price
) {}
