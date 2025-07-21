package com.nexus.seoulmate.domain.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum University {
    SUNGSIL("숭실대학교"),
    SOOKMYUNG("숙명여자대학교");

    private final String displayName;

    University(String displayName){
        this.displayName = displayName;
    }

    @JsonValue
    public String etDisplayName(){
        return displayName;
    }
}
