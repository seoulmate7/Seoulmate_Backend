package com.nexus.seoulmate.member.dto;

import com.nexus.seoulmate.member.domain.enums.Role;
import com.nexus.seoulmate.member.domain.enums.UserStatus;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InProgressResponse {
    private Role role;
    private VerificationStatus univVerification;
    private String jsessionId;
}
