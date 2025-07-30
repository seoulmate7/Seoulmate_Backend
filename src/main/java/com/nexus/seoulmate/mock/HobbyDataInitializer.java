package com.nexus.seoulmate.mock;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(1)
@RequiredArgsConstructor
public class HobbyDataInitializer {

    private final HobbyRepository hobbyRepository;

    private static final Map<HobbyCategory, List<String>> HOBBY_DATA = Map.of(
            HobbyCategory.SPORTS, List.of("축구", "야구", "농구", "탁구", "당구", "복싱", "테니스", "스케이트보드", "스키/보드", "스케이트", "볼링", "요가", "필라테스", "클라이밍", "자전거", "러닝", "미식축구", "수영", "무술", "카레이싱", "크로스핏"),
            HobbyCategory.PARTY, List.of("클러빙", "펍", "홈파티", "생일파티", "페스티벌"),
            HobbyCategory.LANGUAGE, List.of("한국어", "영어", "일본어", "중국어", "스페인어", "프랑스어", "독일어", "스웨덴어", "베트남어", "태국어", "미얀마어", "언어교환"),
            HobbyCategory.ACTIVITY, List.of("시내", "시골/근교", "캠핑", "캐러반", "해외", "로드트립", "낚시", "트래킹", "등산", "서핑", "보드게임", "스포츠관람", "방탈출", "온천", "계곡", "바다"),
            HobbyCategory.CULTURE_ART, List.of("영화", "콘서트", "뮤지컬", "전시회", "k-pop", "패션", "박물관", "갤러리", "쇼핑"),
            HobbyCategory.HOBBY, List.of("춤", "노래", "그림", "게임", "독서", "카페", "자기계발"),
            HobbyCategory.FOOD_DRINK, List.of("맛집투어", "한식", "스시", "동남아음식", "브런치", "디저트", "커피", "와인", "맥주", "채식", "칵테일", "위스키"),
            HobbyCategory.MUSIC, List.of("10년대", "팝송", "EDM", "하우스", "J-pop", "K-pop", "R&B", "레게", "락", "재즈", "인디", "힙합", "오페라", "클래식")
    );

    @PostConstruct
    public void init() {
        for (Map.Entry<HobbyCategory, List<String>> entry : HOBBY_DATA.entrySet()) {
            HobbyCategory category = entry.getKey();
            List<String> hobbies = entry.getValue();

            for (String hobbyName : hobbies) {
                if (hobbyRepository.findByHobbyNameAndHobbyCategory(hobbyName, category).isEmpty()) {
                    hobbyRepository.save(Hobby.builder()
                            .hobbyName(hobbyName)
                            .hobbyCategory(category)
                            .build());
                    System.out.println("취미 리스트 생성 완료");
                } else {
                    System.out.println("취미 리스트가 이미 존재합니다.");
                }
            }
        }
    }

}
