package com.nexus.seoulmate.meeting.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetingSearchReq {

    @Schema(description = "카테고리", example = "문화교류")
    private String category;

    @Schema(description = "키워드", example = "언어")
    private String keyword;

    @Schema(description = "언어", example = "한국어")
    private String language;

    @Schema(description = "언어 레벨", example = "60")
    private Integer languageLevel; // 언어 레벨 0~100
}
