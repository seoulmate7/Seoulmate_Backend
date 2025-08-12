package com.nexus.seoulmate.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;

import java.util.stream.Stream;

public enum Languages {
    ENGLISH("영어"),
    KOREAN("한국어"),
    JAPANESE("일본어"),
    CHINESE("중국어"),
    SPANISH("스페인어"),
    FRENCH("프랑스어"),
    GERMAN("독일어"),
    ITALIAN("이탈리아어"),
    SWEDISH("스웨덴어"),
    VIETNAMESE("베트남어"),
    THAI("태국어"),
    MYANMAR("미얀마어"),
    DUTCH("네덜란드어"),
    NEPALI("네팔어"),
    NORWEGIAN("노르웨이어"),
    RUSSIAN("러시아어"),
    LANGUAGE_EXCHANGE("언어교환");


    private final String displayName;

    Languages(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    // 역직렬화
    @JsonCreator
    public static Languages fromDisplayName(String input) {
        for (Languages l : values()) {
            if (l.getDisplayName().equalsIgnoreCase(input) || l.name().equalsIgnoreCase(input)) {
                return l;
            }
        }
        throw new CustomException(ErrorStatus.INVALID_LANGUAGE, input);
    }
}
