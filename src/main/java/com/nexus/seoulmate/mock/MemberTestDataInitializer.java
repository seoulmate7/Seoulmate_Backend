package com.nexus.seoulmate.mock;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.*;
import com.nexus.seoulmate.member.repository.HobbyRepository;
import com.nexus.seoulmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(2) // 실행 순서
@RequiredArgsConstructor
public class MemberTestDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final HobbyRepository hobbyRepository;

    @Override
    public void run(String... args) {
        log.info("테스트 사용자 데이터 초기화 시작");
        
        List<Member> membersToSave = new ArrayList<>();
        
        // 첫 번째 테스트 사용자
        String testEmail1 = "test1@example.com";
        if (memberRepository.findByEmail(testEmail1).isEmpty()) {

            Map<String, Integer> languages1 = new HashMap<>();
            languages1.put("Korean", 90);
            languages1.put("English", 70);

            // Hobby 엔티티 3개 조회
            Hobby hobby1 = hobbyRepository.findByHobbyNameAndHobbyCategory("축구", HobbyCategory.SPORTS).orElse(null);
            Hobby hobby2 = hobbyRepository.findByHobbyNameAndHobbyCategory("노래", HobbyCategory.HOBBY).orElse(null);
            Hobby hobby3 = hobbyRepository.findByHobbyNameAndHobbyCategory("맛집투어", HobbyCategory.FOOD_DRINK).orElse(null);

            List<Hobby> selectedHobbies1 = new ArrayList<>();
            if (hobby1 != null) selectedHobbies1.add(hobby1);
            if (hobby2 != null) selectedHobbies1.add(hobby2);
            if (hobby3 != null) selectedHobbies1.add(hobby3);

            Member member1 = Member.createGoogleUser(
                    "test1@example.com",
                    "길동",
                    "홍",
                    LocalDate.of(1990, 1, 1),
                    Countries.KOREA,
                    "안녕하세요! 저는 홍길동입니다. 새로운 친구를 만나고 싶어요.",
                    "https://example.com/profile1.jpg",
                    selectedHobbies1,
                    "https://example.com/cert1.jpg",
                    University.SOOKMYUNG,
                    languages1,
                    VerificationStatus.VERIFIED,
                    AuthProvider.LOCAL
            );
            membersToSave.add(member1);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail1);
        }

        // 두 번째 테스트 사용자
        String testEmail2 = "test2@example.com";
        if (memberRepository.findByEmail(testEmail2).isEmpty()) {

            Map<String, Integer> languages2 = new HashMap<>();
            languages2.put("Korean", 75);
            languages2.put("English", 99);
            languages2.put("Japanese", 60);

            // 두 번째 사용자를 위한 다른 취미들
            Hobby hobby4 = hobbyRepository.findByHobbyNameAndHobbyCategory("노래", HobbyCategory.HOBBY).orElse(null);
            Hobby hobby5 = hobbyRepository.findByHobbyNameAndHobbyCategory("와인", HobbyCategory.FOOD_DRINK).orElse(null);
            Hobby hobby6 = hobbyRepository.findByHobbyNameAndHobbyCategory("한국어", HobbyCategory.LANGUAGE).orElse(null);

            List<Hobby> selectedHobbies2 = new ArrayList<>();
            if (hobby4 != null) selectedHobbies2.add(hobby4);
            if (hobby5 != null) selectedHobbies2.add(hobby5);
            if (hobby6 != null) selectedHobbies2.add(hobby6);

            Member member2 = Member.createGoogleUser(
                    "test2@example.com",
                    "Robert",
                    "Daune",
                    LocalDate.of(1995, 5, 15),
                    Countries.USA,
                    "I'm interested in learning Korea. I wanna travel the world!",
                    "https://example.com/profile2.jpg",
                    selectedHobbies2,
                    "https://example.com/cert2.jpg",
                    University.YONSEI,
                    languages2,
                    VerificationStatus.VERIFIED,
                    AuthProvider.LOCAL
            );
            membersToSave.add(member2);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail2);
        }
        
        if (!membersToSave.isEmpty()) {
            memberRepository.saveAll(membersToSave);
            log.info("테스트 사용자 {}명 생성 완료", membersToSave.size());
        } else {
            log.info("모든 테스트 사용자가 이미 존재합니다.");
        }
    }
}
