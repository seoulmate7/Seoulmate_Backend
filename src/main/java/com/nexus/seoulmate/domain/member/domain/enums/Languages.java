package com.nexus.seoulmate.domain.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;

import java.util.stream.Stream;

public enum Languages {
    ENGLISH("ENGLISH"),
    KOREAN("한국어"),
    JAPANESE("일본어"),
    CHINESE("중국어"),
    SPANISH("스페인어"),
    FRENCH("프랑스어"),
    GERMAN("독일어"),
    SWEDISH("스웨덴어"),
    VIETNAMESE("베트남어"),
    THAI("태국어"),
    MYANMAR("미얀마어"),
    LANGUAGE_EXCHANGE("언어교환");


    private final String displayName;

    Languages(String displayName){
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName(){
        return displayName;
    }

    // 역직렬화
    @JsonCreator
    public static Languages fromDisplayName(String input){
        return Stream.of(Languages.values())
                .filter(lang -> lang.displayName.equalsIgnoreCase(input) || lang.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorStatus.INVALID_LANGUAGE));
    }
}
