package com.nexus.seoulmate.member.dto.signup;

import com.nexus.seoulmate.member.domain.enums.Countries;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ProfileCreateRequest {
    private String googleId;
    private String firstName;
    private String lastName;
    private LocalDate DOB;
    private Countries country;
    private String bio;
    private String profileImageUrl;

    // 프로필 생성
    @Builder
    public ProfileCreateRequest(String googleId, String firstName, String lastName, LocalDate DOB,
                                Countries country, String bio, String profileImageUrl){
        this.googleId = googleId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
        this.country = country;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }
}
