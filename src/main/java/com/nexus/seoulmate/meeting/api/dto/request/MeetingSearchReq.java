package com.nexus.seoulmate.meeting.api.dto.request;

import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.Languages;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record MeetingSearchReq(
        @Schema(description = "상위 카테고리", example = "스포츠")
        HobbyCategory hobbyCategory,

        @Schema(description = "키워드", example = "언어")
        String keyword,

        @Schema(description = "언어", example = "한국어")
        Languages language, // 특정 언어 필터

        @Schema(description = "언어 레벨 최솟값", example = "40")
        Integer minLevel, // 언어 레벨 0~100

        @Schema(description = "언어 레벨 최대값", example = "80")
        Integer maxLevel,

        @Schema(description = "페이지 번호(0부터)", example = "0")
        Integer page,

        @Schema(description = "페이지당 개수", example = "20")
        Integer size
) {

    public int pageDefault() { return page == null || page < 0 ? 0 : page; }
    public int sizeDefault() { return size == null || size < 1 ? 20 : size; }

    public int minLevelDefault() { return minLevel == null ? 0 : Math.max(0, Math.min(100, minLevel)); }
    public int maxLevelDefault() { return maxLevel == null ? 100 : Math.max(0, Math.min(100, maxLevel)); }
}
