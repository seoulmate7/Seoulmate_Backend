package com.nexus.seoulmate.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Countries {
    KOREA("대한민국"),
    USA("미국"),
    CHINA("중국"),
    JAPAN("일본"),
    NETHERLANDS("네덜란드"),
    NEPAL("네팔"),
    NORWAY("노르웨이"),
    GERMANY("독일"),
    RUSSIA("러시아"),
    MONGOLIA("몽골"),
    BANGLADESH("방글라데시"),
    VIETNAM("베트남"),
    BELGIUM("벨기에"),
    SWEDEN("스웨덴"),
    SWITZERLAND("스위스"),
    SPAIN("스페인"),
    UK("영국"),
    AUSTRIA("오스트리아"),
    UZBEKISTAN("우즈베키스탄"),
    ITALY("이탈리아"),
    INDIA("인도"),
    INDONESIA("인도네시아"),
    KAZAKHSTAN("카자흐스탄"),
    CANADA("캐나다"),
    THAILAND("태국"),
    PAKISTAN("파키스탄"),
    FRANCE("프랑스"),
    PHILIPPINES("필리핀"),
    AUSTRALIA("호주");

    private final String displayName;

    Countries(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    // 한글 표시명으로부터 enum을 찾는 메서드
    public static Countries fromDisplayName(String displayName) {
        for (Countries country : values()) {
            if (country.getDisplayName().equals(displayName)) {
                return country;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 국가입니다: " + displayName);
    }
}
