package com.nexus.seoulmate.member.dto.signup;

import com.nexus.seoulmate.member.domain.enums.University;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnivAuthRequest {
    private String googleId;
    private University university;
    private String univCertificate;
    private VerificationStatus verificationStatus;

    @Builder
    public UnivAuthRequest(String googleId, University university, String univCertificate){
        this.googleId = googleId;
        this.university = university;
        this.univCertificate = univCertificate;
        verificationStatus = VerificationStatus.SUBMITTED;
    }
}
