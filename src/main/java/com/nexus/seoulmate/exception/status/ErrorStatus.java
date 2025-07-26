package com.nexus.seoulmate.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    // COMMON 4XX
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400", "파라미터가 올바르지 않습니다."),
    INVALID_BODY(HttpStatus.BAD_REQUEST, "COMMON400", "요청 본문이 올바르지 않습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "찾을 수 없는 리소스입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "사용자를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않는 HTTP Method입니다."),

    // 도메인별로

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "사용자를 찾을 수 없습니다."),

    // Meeting
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING404", "모임을 찾을 수 없습니다."),
    INVALID_MEETING_TYPE(HttpStatus.BAD_REQUEST, "MEETINGTYPE400", "모임 타입이 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
