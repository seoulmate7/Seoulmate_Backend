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

            Map<Languages, Integer> languages1 = new HashMap<>();
            languages1.put(Languages.KOREAN, 90);
            languages1.put(Languages.ENGLISH, 70);

            List<Hobby> selectedHobbies1 = hobbyRepository.findByHobbyNameIn(List.of(new String[]{"축구", "노래", "스케이트보드"}));

            Member member1 = Member.createLocalUser(
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
                    VerificationStatus.VERIFIED
            );
            membersToSave.add(member1);
        } else {
            log.info("{} 계정은 이미 존재합니다.", testEmail1);
        }

        // 두 번째 테스트 사용자
        String testEmail2 = "test2@example.com";
        if (memberRepository.findByEmail(testEmail2).isEmpty()) {

            Map<Languages, Integer> languages2 = new HashMap<>();
            languages2.put(Languages.KOREAN, 75);
            languages2.put(Languages.ENGLISH, 99);
            languages2.put(Languages.JAPANESE, 60);
            
            List<Hobby> selectedHobbies2 = hobbyRepository.findByHobbyNameIn(List.of(new String[]{"노래", "와인", "한국어"}));

            Member member2 = Member.createLocalUser(
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
                    VerificationStatus.VERIFIED
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
