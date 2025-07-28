package com.nexus.seoulmate.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    // SUCCESS 2XX
    SUCCESS(HttpStatus.OK, "COMMON200", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "리소스가 성공적으로 생성되었습니다."),

    // 도메인별로
    // 회원가입
    LEVEL_TEST_SUCCESS(HttpStatus.OK, "SIGNUP 200", "레벨테스트 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
