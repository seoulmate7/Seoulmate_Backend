package com.nexus.seoulmate.mock;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class HobbyDataInitializer {

    private final HobbyRepository hobbyRepository;

    public static final Map<HobbyCategory, List<String>> HOBBY_DATA = Map.of(
            HobbyCategory.SPORTS, List.of("축구", "야구", "농구", "탁구", "당구", "복싱", "테니스", "스케이트보드", "스키/보드", "스케이트", "볼링", "요가", "필라테스", "클라이밍", "자전거", "러닝", "미식축구", "수영", "무술", "카레이싱", "크로스핏"),
            HobbyCategory.PARTY, List.of("클러빙", "펍", "홈파티", "생일파티", "페스티벌"),
            HobbyCategory.LANGUAGE, List.of("한국어", "영어", "일본어", "중국어", "스페인어", "프랑스어", "독일어", "스웨덴어", "베트남어", "태국어", "미얀마어", "언어교환"),
            HobbyCategory.ACTIVITY, List.of("시내", "시골/근교", "캠핑", "캐러반", "해외", "로드트립", "낚시", "트래킹", "등산", "서핑", "보드게임", "스포츠관람", "방탈출", "온천", "계곡", "바다"),
            HobbyCategory.CULTURE_ART, List.of("영화", "콘서트", "뮤지컬", "전시회", "패션", "박물관", "갤러리", "쇼핑"), // k-pop 제거 (중복)
            HobbyCategory.HOBBY, List.of("춤", "노래", "그림", "게임", "독서", "카페", "자기계발"),
            HobbyCategory.FOOD_DRINK, List.of("맛집투어", "한식", "스시", "동남아음식", "브런치", "디저트", "커피", "와인", "맥주", "채식", "칵테일", "위스키"),
            HobbyCategory.MUSIC, List.of("10년대", "팝송", "EDM", "하우스", "J-pop", "K-pop", "R&B", "레게", "락", "재즈", "인디", "힙합", "오페라", "클래식")
    );

//    @PostConstruct
//    public void init() {
//        log.info("취미 데이터 초기화 시작");
//
//        // 모든 기존 조회
//        List<Hobby> existingHobbies = hobbyRepository.findAll();
//        Set<String> existingHobbyKeys = existingHobbies.stream()
//                .map(hobby -> hobby.getHobbyName() + ":" + hobby.getHobbyCategory())
//                .collect(Collectors.toSet());
//
//        List<Hobby> hobbiesToSave = new ArrayList<>();
//
//        // 모든 취미를 한 번에 처리
//        for (Map.Entry<HobbyCategory, List<String>> entry : HOBBY_DATA.entrySet()) {
//            HobbyCategory category = entry.getKey();
//            List<String> hobbyNames = entry.getValue();
//
//            for (String hobbyName : hobbyNames) {
//                String hobbyKey = hobbyName + ":" + category;
//                if (!existingHobbyKeys.contains(hobbyKey)) {
//                    hobbiesToSave.add(Hobby.builder()
//                            .hobbyName(hobbyName)
//                            .hobbyCategory(category)
//                            .build());
//                }
//            }
//        }
//
//        if (!hobbiesToSave.isEmpty()) {
//            hobbyRepository.saveAll(hobbiesToSave);
//            log.info("취미 데이터 {}개 생성 완료", hobbiesToSave.size());
//        } else {
//            log.info("모든 취미 데이터가 이미 존재합니다.");
//        }
//    }
}
