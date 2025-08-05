package com.nexus.seoulmate.member.dto;

import com.nexus.seoulmate.member.domain.enums.UserStatus;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {
    private VerificationStatus univVerification;
    private UserStatus userStatus;
    private String googleId;
}
