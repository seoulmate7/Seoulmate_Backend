package com.nexus.seoulmate.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HobbyUpdateRequest {
    @Schema(description = "취미 목록", example = "[\"축구\", \"스페인어\", \"댄스\"]")
    private List<String> hobbies;
}
