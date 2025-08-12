package com.nexus.seoulmate.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;

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

    @JsonCreator
    public static HobbyCategory fromDisplayName(String input){
        for (HobbyCategory category : values()) {
            if(category.displayName.equalsIgnoreCase(input) || category.name().equalsIgnoreCase(input)){
                return category;
            }
        }
        throw  new CustomException(ErrorStatus.CATEGORY_NOT_FOUND, input);
    }
}
