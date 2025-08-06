package com.nexus.seoulmate.member.dto.signup;

import com.nexus.seoulmate.member.domain.enums.University;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "대학교 인증 요청")
public class UnivAuthDto {
    private University university;

    private String univCertificateUrl;

    @Builder
    public UnivAuthDto(University university, String univCertificateUrl){
        this.university = university;
        this.univCertificateUrl = univCertificateUrl;    }
}
