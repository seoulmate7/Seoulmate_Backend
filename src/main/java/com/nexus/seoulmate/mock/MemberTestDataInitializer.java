package com.nexus.seoulmate.mock;

import com.nexus.seoulmate.domain.member.domain.Hobby;
import com.nexus.seoulmate.domain.member.domain.Member;
import com.nexus.seoulmate.domain.member.domain.enums.*;
import com.nexus.seoulmate.domain.member.repository.HobbyRepository;
import com.nexus.seoulmate.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(2) // 실행 순서
@RequiredArgsConstructor
public class MemberTestDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final HobbyRepository hobbyRepository;

    @Override
    public void run(String... args) {
        String testEmail = "test@example.com";
        if (memberRepository.findByEmail(testEmail).isEmpty()) {

            Map<String, Integer> languages = new HashMap<>();
            languages.put("Korean", 90);
            languages.put("English", 70);

            // Hobby 엔티티 3개 조회 (이름과 카테고리 기준)
            Hobby hobby1 = hobbyRepository.findByHobbyNameAndHobbyCategory("축구", HobbyCategory.SPORTS).orElse(null);
            Hobby hobby2 = hobbyRepository.findByHobbyNameAndHobbyCategory("노래", HobbyCategory.HOBBY).orElse(null);
            Hobby hobby3 = hobbyRepository.findByHobbyNameAndHobbyCategory("맛집투어", HobbyCategory.FOOD_DRINK).orElse(null);

            List<Hobby> selectedHobbies = new ArrayList<>();
            if (hobby1 != null) selectedHobbies.add(hobby1);
            if (hobby2 != null) selectedHobbies.add(hobby2);
            if (hobby3 != null) selectedHobbies.add(hobby3);

            Member member = Member.createGoogleUser(
                    "test@example.com",
                    "길동",
                    "홍",
                    LocalDate.of(1990, 1, 1),
                    Countries.KOREA,
                    "한줄 자기소개",
                    "https://example.com/profile.jpg",
                    selectedHobbies,
                    "https://example.com/cert.jpg",
                    University.SOOKMYUNG,
                    languages,
                    VerificationStatus.VERIFIED,
                    AuthProvider.GOOGLE
            );
            memberRepository.save(member);
            System.out.println("테스트 사용자 생성 완료");
        } else {
            System.out.println(testEmail + " 계정은 이미 존재합니다.");
        }
    }
}
