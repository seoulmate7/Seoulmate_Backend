package com.nexus.seoulmate.member.dto.signup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HobbyRequest {
    @Schema(description = "취미 목록", example = "[\"축구\", \"한국어\", \"노래\"]")
    private List<String> hobbies;
}
