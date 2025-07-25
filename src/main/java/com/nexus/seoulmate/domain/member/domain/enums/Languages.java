package com.nexus.seoulmate.domain.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Languages {
    ENGLISH("ENGLISH"),
    KOREAN("한국어");

    private final String displayName;

    Languages(String displayName){
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName(){
        return displayName;
    }
}
