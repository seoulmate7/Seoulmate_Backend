package com.nexus.seoulmate.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthProvider {
    LOCAL("일반 가입"),
    GOOGLE("구글");

    private final String description;

    AuthProvider(String description){
        this.description = description;
    }

    @JsonValue
    public String getAuthProvider(){
        return description;
    }
}
