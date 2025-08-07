package com.nexus.seoulmate.exception.status;

import com.google.api.Http;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    // SUCCESS 2XX
    SUCCESS(HttpStatus.OK, "COMMON200", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "리소스가 성공적으로 생성되었습니다."),

    // 도메인별로
    // 회원가입
    LEVEL_TEST_SUCCESS(HttpStatus.OK, "SIGNUP 200", "레벨테스트 성공했습니다."),

    // Meeting
    CREATE_MEETING(HttpStatus.CREATED, "MEETING201", "모임이 성공적으로 생성되었습니다."),
    UPDATE_MEETING(HttpStatus.OK, "MEETING200","모임이 성공적으로 수정되었습니다."),
    DELETE_MEETING(HttpStatus.OK, "MEETING200","모임이 성공적으로 삭제되었습니다."),
    READ_MEETING_DETAIL(HttpStatus.OK, "MEETING200","모임 상세 조회에 성공했습니다."),

    // Search
    SEARCH_SUCCESS(HttpStatus.OK, "COMMON202", "조회에 성공하였습니다."),

    // Payment
    CREATE_ORDER(HttpStatus.CREATED, "ORDER201", "주문이 성공적으로 생성되었습니다."),
    VERIFY_PAYMENT(HttpStatus.OK, "PAYMENT200", "결제 검증 성공."),
    PAYMENT_WEBHOOK_RECEIVED(HttpStatus.OK, "PAYMENT_WEBHOOK200", "웹훅 수신 완료."),
    GET_ORDER(HttpStatus.OK, "ORDER200", "주문 정보 조회에 성공했습니다."),
    PAYMENT_SUCCESS(HttpStatus.OK, "PAYMENT200", "결제에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
