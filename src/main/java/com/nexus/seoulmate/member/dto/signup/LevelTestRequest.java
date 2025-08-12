package com.nexus.seoulmate.member.dto.signup;

import com.nexus.seoulmate.member.domain.enums.Languages;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@Schema(description = "언어 레벨 테스트 요청")
public class LevelTestRequest {
    @Schema(description = "언어별 레벨", example = "{\"한국어\": 90, \"영어\": 82}")
    private Map<Languages, Integer> languages = new HashMap<>();
}
