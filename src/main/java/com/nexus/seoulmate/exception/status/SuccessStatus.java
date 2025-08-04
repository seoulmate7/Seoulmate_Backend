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

    // Member
    GOOGLE_PROFILE_SUCCESS(HttpStatus.OK, "SIGNUP 200", "구글 회원가입 성공"),
    PROFILE_INFO_SUCCESS(HttpStatus.OK, "SIGNUP 200", "프로필 기본 정보 불러오기 성공"),
    PROFILE_SUCCESS(HttpStatus.OK, "SIGNUP 200", "프로필 생성 성공"),
    PROFILE_IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "SIGNUP 200", "프로필 이미지 업로드 성공"),
    LEVEL_TEST_SUCCESS(HttpStatus.OK, "FLUENT 200", "레벨테스트 성공"),
    SUBMIT_LEVEL_TEST_SUCCESS(HttpStatus.OK, "SIGNUP 200", "레벨테스트 제출 성공"),
    HOBBY_SUCCESS(HttpStatus.OK, "SIGNUP 200", "취미 선택 완료"),
    // REGISTER_SUCCESS(HttpStatus.OK, "SIGNUP 200", "학교 인증 신청 성공"),
    MEMBER_CREATED(HttpStatus.CREATED, "SIGNUP 201", "회원가입 성공"),

    // Meeting
    CREATE_MEETING(HttpStatus.CREATED, "MEETING201", "모임이 성공적으로 생성되었습니다."),
    UPDATE_MEETING(HttpStatus.OK, "MEETING200","모임이 성공적으로 수정되었습니다."),
    DELETE_MEETING(HttpStatus.OK, "MEETING200","모임이 성공적으로 삭제되었습니다."),
    READ_MEETING_DETAIL(HttpStatus.OK, "MEETING200","모임 상세 조회에 성공했습니다."),

    // Search
    SEARCH_SUCCESS(HttpStatus.OK, "COMMON202", "조회에 성공하였습니다."),

    // Friend
    FRIEND_REQUEST_SENT(HttpStatus.CREATED, "FRIEND201", "친구 요청이 성공적으로 전송되었습니다."),
    FRIEND_REQUEST_UPDATED(HttpStatus.OK, "FRIEND200", "친구 요청 상태가 성공적으로 업데이트되었습니다."),
    FRIEND_REQUEST_LIST_FETCHED(HttpStatus.OK, "FRIEND200", "친구 요청 목록 조회 성공"),
    FRIEND_LIST_FETCHED(HttpStatus.OK, "FRIEND200", "친구 목록 조회 성공"),
    FRIEND_DETAIL_FETCHED(HttpStatus.OK, "FRIEND200", "사용자 상세 정보 조회 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
