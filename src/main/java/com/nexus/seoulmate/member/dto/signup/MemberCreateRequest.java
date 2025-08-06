package com.nexus.seoulmate.member.dto.signup;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.enums.AuthProvider;
import com.nexus.seoulmate.member.domain.enums.Countries;
import com.nexus.seoulmate.member.domain.enums.University;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateRequest {

    private String googleId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate DOB; // 생년월일

    private Countries country; // 국가 정보
    private String bio;

    private String profileImage;
    private List<Hobby> hobbies;

    private String univCertificate;
    private University univ;

    private Map<String, Integer> languages;

    private VerificationStatus verificationStatus; // 인증 여부
    private AuthProvider authProvider; // GOOGLE, KAKAO 등
}
