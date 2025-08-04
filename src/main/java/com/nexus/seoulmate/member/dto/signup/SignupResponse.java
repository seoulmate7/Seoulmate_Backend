package com.nexus.seoulmate.member.dto.signup;

import com.nexus.seoulmate.member.domain.enums.AuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupResponse {
    private String googleId;
    private String email;
    private String firstName;
    private String lastName;
    private String authProvider; // google
    private String sessionId; // JSESSIONID 받아오기

    // 구글 회원가입
    @Builder
    public SignupResponse(String googleId, String email, String firstName, String lastName, String sessionId){
        this.googleId = googleId;
        this.email = email; // 다시 수정할 일 없음
        this.firstName = firstName;
        this.lastName = lastName;
        authProvider = String.valueOf(AuthProvider.GOOGLE);
        this.sessionId = sessionId;
    }

    public SignupResponse(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
