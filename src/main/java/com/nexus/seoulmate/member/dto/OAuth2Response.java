package com.nexus.seoulmate.member.dto;

import java.util.Map;

public interface OAuth2Response {

    // 제공자
    String getProvider();

    // 제공자에서 발급해주는 아이디
    String getProviderId();

    // 이메일
    String getEmail();

    // 사용자 실명
    String getName();

    // 사용자 이름
    String getGivenName();

    // 사용자 성
    String getFamilyName();

    // OAuth2 속성 정보
    Map<String, Object> getAttributes();
}


// { resultcode=00, message=success, id=123123123, name=개발자유미}