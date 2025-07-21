package com.nexus.seoulmate.domain.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HobbyCategory {
    SPORTS("스포츠"),
    PARTY("파티"),
    LANGUAGE("언어"),
    ACTIVITY("액티비티"),
    CULTURE_ART("문화/예술"),
    HOBBY("취미"),
    FOOD_DRINK("음식/드링크"),
    MUSIC("음악");

    private final String displayName;

    HobbyCategory(String displayName){
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName(){
        return displayName;
    }
}
