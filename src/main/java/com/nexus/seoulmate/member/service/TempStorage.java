package com.nexus.seoulmate.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.enums.AuthProvider;
import com.nexus.seoulmate.member.domain.enums.Countries;
import com.nexus.seoulmate.member.domain.enums.University;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;
import com.nexus.seoulmate.member.domain.enums.*;
import com.nexus.seoulmate.member.dto.signup.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TempStorage {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Duration TTL = Duration.ofMinutes(30); // 저장 시간

    // 1. 구글 회원가입 정보 저장
    public void save(SignupResponse dto) {
        String key = dto.getGoogleId();
        Map<String, Object> map = Map.of(
                "sessionId", dto.getSessionId(),
                "email", dto.getEmail(),
                "firstName", dto.getFirstName(),
                "lastName", dto.getLastName(),
                "authProvider", dto.getAuthProvider()
        );
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, TTL);
        System.out.println("Redis에 저장된 세션 ID: " + dto.getSessionId());
    }

    // 1-1. 구글 회원가입 정보 가져오기
    public SignupResponse getSignupResponse(String googleId) {
        try {
            if (googleId == null || googleId.isEmpty()) {
                System.out.println("googleId가 null이거나 비어있습니다.");
                return null;
            }
            
            Map<Object, Object> raw = redisTemplate.opsForHash().entries(googleId);
            System.out.println("Redis에서 가져온 데이터: " + raw);
            
            if (raw.isEmpty()) {
                System.out.println("Redis에서 데이터를 찾을 수 없습니다. googleId: " + googleId);
                return null;
            }
            
            String sessionId = (String) raw.get("sessionId");
            String email = (String) raw.get("email");
            String firstName = (String) raw.get("firstName");
            String lastName = (String) raw.get("lastName");
            
            System.out.println("Redis에서 추출한 데이터:");
            System.out.println("sessionId: " + sessionId);
            System.out.println("email: " + email);
            System.out.println("firstName: " + firstName);
            System.out.println("lastName: " + lastName);
            
            return SignupResponse.builder()
                    .sessionId(sessionId)
                    .googleId(googleId)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
        } catch (Exception e) {
            System.out.println("getSignupResponse에서 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 2. 프로필 생성 정보 저장
    public void save(ProfileCreateRequest dto, String profileImageUrl){
        String key = dto.getGoogleId();
        Map<String, Object> map = Map.of(
                "firstName", dto.getFirstName(),
                "lastName", dto.getLastName(),
                "DOB", dto.getDOB().toString(),
                "bio", dto.getBio(),
                "country", dto.getCountry(),
                "profileImage", profileImageUrl
        );
        redisTemplate.opsForHash().putAll(key, map);
    }

    // 3. 언어 테스트 정보 저장
    public void save(LevelTestRequest dto){
        String key = dto.getGoogleId();
        redisTemplate.opsForHash().put(key, "languages", dto.getLanguages());
    }

    // 4. 취미 저장
    public void save(HobbyRequest dto){
        String key = dto.getGoogleId();
        // List<String>을 그대로 저장
        redisTemplate.opsForHash().put(key, "hobbies", dto.getHobbies());
    }

    // 5. 학교 인증 신청
    public void save(UnivAuthRequest dto){
        String key = dto.getGoogleId();
        Map<String, Object> map = Map.of(
                "univ", dto.getUniversity(),
                "univCertificate", dto.getUnivCertificate(),
                "verificationStatus", dto.getVerificationStatus()
        );
        redisTemplate.opsForHash().putAll(key, map);
    }

    // 최종 데이터 취합
    public MemberCreateRequest collect(String googleId){
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(googleId);
        MemberCreateRequest result = new MemberCreateRequest();

        // sessionId도 함께 로그 출력
        String sessionId = (String) raw.get("sessionId");
        System.out.println("최종 데이터 취합 시 세션 ID: " + sessionId);

        result.setEmail((String) raw.get("email"));
        result.setFirstName((String) raw.get("firstName"));
        result.setLastName((String) raw.get("lastName"));
        
        // DOB null 체크
        Object dobObj = raw.get("DOB");
        if (dobObj != null) {
            result.setDOB(LocalDate.parse((String) dobObj));
        }
        
        // Countries enum 처리
        Object countryObj = raw.get("country");
        if (countryObj instanceof Countries) {
            result.setCountry((Countries) countryObj);
        } else {
            result.setCountry(Countries.valueOf((String) countryObj));
        }
        
        result.setBio((String) raw.get("bio"));
        result.setProfileImage((String) raw.get("profileImage"));
        // Hobby 객체 변환
        Object hobbiesObj = raw.get("hobbies");
        if (hobbiesObj instanceof List) {
            List<String> hobbyNames = (List<String>) hobbiesObj;
            List<Hobby> hobbies = hobbyNames.stream()
                    .map(name -> Hobby.builder()
                            .hobbyName(name)
                            .hobbyCategory(HobbyCategory.HOBBY) // 기본값으로 HOBBY 설정
                            .build())
                    .toList();
            result.setHobbies(hobbies);
        }
        result.setUnivCertificate((String) raw.get("univCertificate"));
        
        // University enum 처리
        Object univObj = raw.get("univ");
        if (univObj instanceof University) {
            result.setUniv((University) univObj);
        } else {
            result.setUniv(University.valueOf((String) univObj));
        }
        
        result.setLanguages((Map<String, Integer>) raw.get("languages"));
        
        // VerificationStatus enum 처리
        Object verificationStatusObj = raw.get("verificationStatus");
        if (verificationStatusObj instanceof VerificationStatus) {
            result.setVerificationStatus((VerificationStatus) verificationStatusObj);
        } else {
            result.setVerificationStatus(VerificationStatus.valueOf((String) verificationStatusObj));
        }
        
        // AuthProvider enum 처리
        Object authProviderObj = raw.get("authProvider");
        if (authProviderObj instanceof AuthProvider) {
            result.setAuthProvider((AuthProvider) authProviderObj);
        } else {
            result.setAuthProvider(AuthProvider.valueOf((String) authProviderObj));
        }

        redisTemplate.delete(googleId); // 임시 데이터 삭제
        return result;
    }
}
