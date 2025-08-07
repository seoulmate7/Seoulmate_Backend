package com.nexus.seoulmate.exception.status;

import com.google.api.Http;
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
    // Fluent 관련 예외
    FLUENT_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "FLUENT 401", "Fluent API 로그인에 실패했습니다."),
    FLUENT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FLUENT 404", "Fluent API 토큰을 찾을 수 없습니다."),
    FLUENT_TOKEN_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FLUENT 500", "Fluent API 토큰 파싱에 실패했습니다."),
    POST_CREATE_FAILED(HttpStatus.BAD_REQUEST, "FLUENT 400", "Fluent API 게시물 생성에 실패했습니다."),
    GET_POSTS_FAILED(HttpStatus.BAD_REQUEST, "FLUENT 400", "Fluent API 게시물 조회에 실패했습니다."),
    // FLUENT_SCORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FLUENT 500", "Fluent API 채점 요청에 실패했습니다."),
    FLUENT_RESULT_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FLUENT 500", "Fluent API 채점 결과 파싱에 실패했습니다."),
    FLUENT_AUDIO_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FLUENT 500", "음성 파일 업로드에 실패했습니다."),
    FLUENT_OVERALL_POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "FLUENT 404", "overall_points를 찾을 수 없습니다."),
    FLUENT_OVERALL_POINT_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FLUENT 500", "overall_points 파싱에 실패했습니다."),


    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "사용자를 찾을 수 없습니다."),

    // Meeting
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING404", "모임을 찾을 수 없습니다."),
    INVALID_MEETING_TYPE(HttpStatus.BAD_REQUEST, "MEETINGTYPE400", "모임 타입이 유효하지 않습니다."),
    INVALID_LANGUAGE(HttpStatus.BAD_REQUEST, "COMMON400", "유효하지 않은 언어입니다."),

    // Search
    SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH404", "검색 조건에 해당하는 값이 없습니다."),

    // Order & Payment
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER404", "주문을 찾을 수 없습니다."),
    ALREADY_PARTICIPATED(HttpStatus.CONFLICT, "Order409", "이미 해당 모임에 참여한 사용자입니다."),
    PAYMENT_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT500", "결제 요청 실패"),
    AMOUNT_TAMPERED(HttpStatus.BAD_REQUEST, "PAYMENT400", "결제 금액이 일치하기 않습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT400", "결제가 실패했습니다."),
    IAMPORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT500", "아임포트 API 오류 발생");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
