package com.nexus.seoulmate.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Countries {
    KOREA("대한민국"),
    USA("미국"),
    CHINA("중국");

    private final String displayName;

    Countries(String displayName){
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName(){
        return displayName;
    }
}
