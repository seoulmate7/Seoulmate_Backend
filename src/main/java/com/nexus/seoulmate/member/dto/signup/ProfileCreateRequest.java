package com.nexus.seoulmate.member.dto.signup;

import com.nexus.seoulmate.member.domain.enums.Countries;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "프로필 생성 요청")
public class ProfileCreateRequest {
    @Schema(description = "이름", example = "홍")
    private String firstName;
    
    @Schema(description = "성", example = "길동")
    private String lastName;
    
    @Schema(description = "생년월일", example = "1995-05-15")
    private LocalDate DOB;
    
    @Schema(description = "국가", example = "KOREA")
    private Countries country;
    
    @Schema(description = "자기소개", example = "안녕하세요! 새로운 친구를 만나고 싶어요.")
    private String bio;

}
