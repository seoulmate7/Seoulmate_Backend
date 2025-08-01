package com.nexus.seoulmate.member.dto.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HobbyRequest {
    private String googleId;
    private List<String> hobbies;
}
