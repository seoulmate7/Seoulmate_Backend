package com.nexus.seoulmate.meeting.api.dto.request;

import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.Languages;
import io.swagger.v3.oas.annotations.media.Schema;

public record MeetingSearchReq(
        @Schema(description = "상위 카테고리", example = "스포츠")
        HobbyCategory hobbyCategory,

        @Schema(description = "키워드", example = "언어")
        String keyword,

        @Schema(description = "언어", example = "한국어")
        Languages language, // 특정 언어 필터

        // 언어 레벨 0~100
        @Schema(description = "한국어 레벨 최솟값", example = "40")
        Integer koMinLevel,
        @Schema(description = "한국어 레벨 최대값", example = "80")
        Integer koMaxLevel,

        @Schema(description = "영어 레벨 최솟값", example = "40")
        Integer enMinLevel,
        @Schema(description = "영어 레벨 최대값", example = "80")
        Integer enMaxLevel,

        @Schema(description = "페이지 번호(0부터)", example = "0")
        Integer page,

        @Schema(description = "페이지당 개수", example = "20")
        Integer size
) {

    private int clamp(Integer v, int low, int high, int def) {
        int x = (v == null ? def : v);
        return Math.max(low, Math.min(high, x));
    }

    // 페이징
    public int pageDefault() { return page == null || page < 0 ? 0 : page; }
    public int sizeDefault() { return size == null || size < 1 ? 20 : size; }

    // 언어별 레벨 기본값
    public int koMinDefault() { return clamp(koMinLevel, 0, 100, 0); }
    public int koMaxDefault() { return clamp(koMaxLevel, 0, 100, 100); }
    public int enMinDefault() { return clamp(enMinLevel, 0, 100, 0); }
    public int enMaxDefault() { return clamp(enMaxLevel, 0, 100, 100); }

    public boolean koreanSelected()  { return language != null && language == Languages.KOREAN; }
    public boolean englishSelected() { return language != null && language == Languages.ENGLISH; }
    public boolean languageNotSelected() { return language == null; }

    // 선택 언어만 필터로, 반대 언어 값 무시
    public MeetingSearchReq normalized(){
        if(koreanSelected()){
            return new MeetingSearchReq(
                    hobbyCategory, keyword, language,
                    koMinLevel, koMaxLevel,
                    null, null,
                    page, size
            );
        }
        if(englishSelected()){
            return new MeetingSearchReq(
                    hobbyCategory, keyword, language,
                    null, null,
                    enMinLevel, enMaxLevel,
                    page, size
            );
        }
        // 언어 미선택 -> 둘 다 유지
        return this;
    }
}
