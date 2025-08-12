package com.nexus.seoulmate.meeting.api.dto.request.officiaReq;

import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공식 모임 수정 요청 DTO")
public record MeetingUpdateOfficialReq(

        @Schema(description = "모임 제목", example = "서울시 공식 언어 교환 모임 - 수정")
        String title,

        @Schema(description = "대표 이미지 URL", example = "https://example.com/official-updated.jpg")
        String image,

        @Schema(description = "장소", example = "서울시청 시민홀")
        String location,

        @Schema(description = "카테고리", example = "파티")
        HobbyCategory hobbyCategory,

        @Schema(description = "모임 날짜 (dd/MM/yyyy)", example = "01/08/2025")
        String meeting_day,

        @Schema(description = "시작 시간 (HH:mm)", example = "17:30")
        String start_time,

        @Schema(description = "최대 인원", example = "150")
        int max_participants,

        @Schema(description = "호스트 메시지", example = "장소가 시민홀로 변경되었습니다!")
        String host_message,

        @Schema(description = "참가비", example = "0")
        int price
) {}
