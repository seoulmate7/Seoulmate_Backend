package com.nexus.seoulmate.domain.member.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum University {
    CATHOLIC("가톨릭대학교"),
    METHODIST("감리교신학대학교"),
    KANGSEO("강서대학교"),
    KONKUK("건국대학교"),
    KYONGGI_SEOUL("경기대학교(서울캠퍼스)"),
    KYUNGHEE("경희대학교"),
    KOREA("고려대학교"),
    KWANGWOON("광운대학교"),
    KOOKMIN("국민대학교"),
    GLE("국제언어대학원대학교"),
    KIA("국제예술대학교"),
    DONGGUK("동국대학교"),
    DONGDUK("동덕여자대학교"),
    DONGYANG("동양미래대학교"),
    MYONGJI("명지대학교"),
    MYONGJI_COLLEGE("명지전문대학"),
    BAEHWA("배화여자대학교"),
    BAI("백석예술대학교"),
    SAMYUK("삼육대학교"),
    SAMYUK_HEALTH("삼육보건대학교"),
    SANGMYUNG("상명대학교"),
    SOGANG("서강대학교"),
    SUKYUNG("서경대학교"),
    SEOULTECH("서울과학기술대학교"),
    SEOUL_EDU("서울교육대학교"),
    SFC("서울기독대학교"),
    SNU("서울대학교"),
    SWCN("서울여자간호대학교"),
    SWU("서울여자대학교"),
    HYU_THEOLOGICAL("서울한영대학교"),
    SEOIL("서일대학교"),
    ANGLICAN("성공회대학교"),
    SKKU("성균관대학교"),
    SUNGSHIN("성신여자대학교"),
    SEJONG("세종대학교"),
    SOOKMYUNG("숙명여자대학교"),
    SUNGEE("숭의여자대학교"),
    SUNGSIL("숭실대학교"),
    YONSEI("연세대학교"),
    EWHA("이화여자대학교"),
    INDUK("인덕대학교"),
    PRESBYTERIAN("장로회신학대학교"),
    CHUNGANG("중앙대학교"),
    CHONGSHIN("총신대학교"),
    CHUGYE("추계예술대학교"),
    KBU("한국성서대학교"),
    HUFS("한국외국어대학교"),
    HANSUNG("한성대학교"),
    HANYANG("한양대학교"),
    HYWC("한양여자대학교"),
    HONGIK("홍익대학교"),
    JEONGHWA("정화예술대학교");

    private final String displayName;

    University(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
