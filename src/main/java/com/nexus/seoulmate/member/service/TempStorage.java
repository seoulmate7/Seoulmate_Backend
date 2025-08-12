package com.nexus.seoulmate.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.seoulmate.member.domain.Hobby;
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
                "googleId", dto.getGoogleId(),
                "email", dto.getEmail(),
                "firstName", dto.getFirstName(),
                "lastName", dto.getLastName(),
                "authProvider", dto.getAuthProvider()
        );
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, TTL);
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

            String email = (String) raw.get("email");
            String firstName = (String) raw.get("firstName");
            String lastName = (String) raw.get("lastName");
            String sessionId = (String) raw.get("sessionId");

            return SignupResponse.builder()
                    .googleId(googleId)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .sessionId(sessionId)
                    .build();
        } catch (Exception e) {
            System.out.println("getSignupResponse에서 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 2. 프로필 생성 정보 저장
    public void save(ProfileCreateRequest dto, String googleId) {
        String key = googleId;
        Map<String, Object> map = Map.of(
                "firstName", dto.getFirstName(),
                "lastName", dto.getLastName(),
                "DOB", dto.getDOB().toString(),
                "bio", dto.getBio(),
                "country", dto.getCountry(),
                "profileImageUrl", dto.getProfileImageUrl()
        );
        redisTemplate.opsForHash().putAll(key, map);
    }

    // 3. 언어 테스트 정보 저장
    public void save(LevelTestRequest dto, String googleId) {
        String key = googleId;
        redisTemplate.opsForHash().put(key, "languages", dto.getLanguages());
    }

    // 4. 취미 저장
    public void save(HobbyRequest dto, String googleId) {
        String key = googleId;
        redisTemplate.opsForHash().put(key, "hobbies", dto.getHobbies());
    }

    // 5. 학교 인증 신청
    public void save(UnivAuthDto dto, String googleId) {
        String key = googleId;
        Map<String, Object> map = Map.of(
                "univ", dto.getUniversity(),
                "univCertificate", dto.getUnivCertificateUrl(),
                "verificationStatus", VerificationStatus.SUBMITTED
        );
        redisTemplate.opsForHash().putAll(key, map);
    }

    // 최종 데이터 취합
    public MemberCreateRequest collect(String googleId) {
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(googleId);

        try {
            String email = (String) raw.get("email");
            String firstName = (String) raw.get("firstName");
            String lastName = (String) raw.get("lastName");
            
            LocalDate dob = null;
            Object dobObj = raw.get("DOB");
            if (dobObj != null) {
                dob = LocalDate.parse((String) dobObj);
            }

            Countries country = null;
            Object countryObj = raw.get("country");
            if (countryObj instanceof Countries) {
                country = (Countries) countryObj;
            } else if (countryObj instanceof String countryStr) {
                try {
                    country = Countries.valueOf(countryStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 country 값: " + countryStr);
                }
            }

            String bio = (String) raw.get("bio");
            String profileImage = (String) raw.get("profileImageUrl");  // profileImage -> profileImageUrl로 변경

            List<Hobby> hobbies = null;
            Object hobbiesObj = raw.get("hobbies");
            if (hobbiesObj instanceof List<?> list) {
                hobbies = list.stream()
                        .filter(item -> item instanceof String)
                        .map(item -> Hobby.builder()
                                .hobbyName((String) item)
                                .hobbyCategory(HobbyCategory.HOBBY)
                                .build())
                        .toList();
            }

            String univCertificate = (String) raw.get("univCertificate");

            University univ = null;
            Object univObj = raw.get("univ");
            if (univObj instanceof University) {
                univ = (University) univObj;
            } else if (univObj instanceof String univStr) {
                try {
                    univ = University.valueOf(univStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 university 값: " + univStr);
                }
            }

            Map<Languages, Integer> languages = null;
            Object langObj = raw.get("languages");
            if (langObj instanceof Map<?, ?> langMap) {
                try {
                    languages = (Map<Languages, Integer>) langMap;
                } catch (ClassCastException e) {
                    System.out.println("languages 형변환 실패: " + langMap);
                }
            }

            VerificationStatus verificationStatus = null;
            Object verificationStatusObj = raw.get("verificationStatus");
            if (verificationStatusObj instanceof VerificationStatus) {
                verificationStatus = (VerificationStatus) verificationStatusObj;
            } else if (verificationStatusObj instanceof String vsStr) {
                try {
                    verificationStatus = VerificationStatus.valueOf(vsStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 verificationStatus 값: " + vsStr);
                }
            }

            AuthProvider authProvider = null;
            Object authProviderObj = raw.get("authProvider");
            if (authProviderObj instanceof AuthProvider) {
                authProvider = (AuthProvider) authProviderObj;
            } else if (authProviderObj instanceof String apStr) {
                try {
                    authProvider = AuthProvider.valueOf(apStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 authProvider 값: " + apStr);
                }
            }

            // 생성자를 사용하여 MemberCreateRequest 객체 생성
            return new MemberCreateRequest(googleId, email, firstName, lastName, dob, country, 
                                        bio, profileImage, hobbies, univCertificate, univ, 
                                        languages, verificationStatus, authProvider);
        } finally {
            redisTemplate.delete(googleId); // 성공/실패 관계없이 삭제
        }
    }
}
