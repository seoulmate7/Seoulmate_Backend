package com.nexus.seoulmate.meeting.api.dto.request.officiaReq;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공식 모임 생성 요청 DTO")
public record MeetingCreateOfficialReq(

        @Schema(description = "모임 제목", example = "서울시 공식 언어 교환 모임")
        String title,

        @Schema(description = "대표 이미지 URL", example = "https://example.com/official-image.jpg")
        String image,

        @Schema(description = "장소", example = "서울시청 앞 광장")
        String location,

        @Schema(description = "카테고리", example = "문화교류")
        String category,

        @Schema(description = "모임 날짜 (dd/MM/yyyy)", example = "30/07/2025")
        String meeting_day,

        @Schema(description = "시작 시간 (HH:mm)", example = "16:00")
        String start_time,

        @Schema(description = "최대 인원", example = "100")
        int max_participants,

        @Schema(description = "호스트 메시지", example = "서울시와 함께하는 문화 소통의 시간!")
        String host_message,

        @Schema(description = "참가비", example = "0")
        int price
) {}
