package com.nexus.seoulmate.member.dto.signup;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class LevelTestRequest {
    private String googleId;
    private Map<String, Integer> languages = new HashMap<>();

    @Builder
    public LevelTestRequest(String googleId, Map<String, Integer> languages) {
        this.googleId = googleId;
        this.languages = languages;
    }
}
