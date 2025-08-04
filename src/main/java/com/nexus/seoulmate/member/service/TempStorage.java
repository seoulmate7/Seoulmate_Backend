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
    public void save(ProfileCreateRequest dto, String profileImageUrl) {
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
    public void save(LevelTestRequest dto) {
        String key = dto.getGoogleId();
        redisTemplate.opsForHash().put(key, "languages", dto.getLanguages());
    }

    // 4. 취미 저장
    public void save(HobbyRequest dto) {
        String key = dto.getGoogleId();
        redisTemplate.opsForHash().put(key, "hobbies", dto.getHobbies());
    }

    // 5. 학교 인증 신청
    public void save(UnivAuthRequest dto) {
        String key = dto.getGoogleId();
        Map<String, Object> map = Map.of(
                "univ", dto.getUniversity(),
                "univCertificate", dto.getUnivCertificate(),
                "verificationStatus", dto.getVerificationStatus()
        );
        redisTemplate.opsForHash().putAll(key, map);
    }

    // 최종 데이터 취합
    @SuppressWarnings("unchecked")
    public MemberCreateRequest collect(String googleId) {
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(googleId);
        MemberCreateRequest result = new MemberCreateRequest();

        try {
            result.setEmail((String) raw.get("email"));
            result.setFirstName((String) raw.get("firstName"));
            result.setLastName((String) raw.get("lastName"));

            Object dobObj = raw.get("DOB");
            if (dobObj != null) {
                result.setDOB(LocalDate.parse((String) dobObj));
            }

            Object countryObj = raw.get("country");
            if (countryObj instanceof Countries) {
                result.setCountry((Countries) countryObj);
            } else if (countryObj instanceof String countryStr) {
                try {
                    result.setCountry(Countries.valueOf(countryStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 country 값: " + countryStr);
                }
            }

            result.setBio((String) raw.get("bio"));
            result.setProfileImage((String) raw.get("profileImage"));

            Object hobbiesObj = raw.get("hobbies");
            if (hobbiesObj instanceof List<?> list) {
                List<Hobby> hobbies = list.stream()
                        .filter(item -> item instanceof String)
                        .map(item -> Hobby.builder()
                                .hobbyName((String) item)
                                .hobbyCategory(HobbyCategory.HOBBY)
                                .build())
                        .toList();
                result.setHobbies(hobbies);
            }

            result.setUnivCertificate((String) raw.get("univCertificate"));

            Object univObj = raw.get("univ");
            if (univObj instanceof University) {
                result.setUniv((University) univObj);
            } else if (univObj instanceof String univStr) {
                try {
                    result.setUniv(University.valueOf(univStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 university 값: " + univStr);
                }
            }

            Object langObj = raw.get("languages");
            if (langObj instanceof Map<?, ?> langMap) {
                try {
                    result.setLanguages((Map<String, Integer>) langMap);
                } catch (ClassCastException e) {
                    System.out.println("languages 형변환 실패: " + langMap);
                }
            }

            Object verificationStatusObj = raw.get("verificationStatus");
            if (verificationStatusObj instanceof VerificationStatus) {
                result.setVerificationStatus((VerificationStatus) verificationStatusObj);
            } else if (verificationStatusObj instanceof String vsStr) {
                try {
                    result.setVerificationStatus(VerificationStatus.valueOf(vsStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 verificationStatus 값: " + vsStr);
                }
            }

            Object authProviderObj = raw.get("authProvider");
            if (authProviderObj instanceof AuthProvider) {
                result.setAuthProvider((AuthProvider) authProviderObj);
            } else if (authProviderObj instanceof String apStr) {
                try {
                    result.setAuthProvider(AuthProvider.valueOf(apStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 authProvider 값: " + apStr);
                }
            }

            return result;
        } finally {
            redisTemplate.delete(googleId); // 성공/실패 관계없이 삭제
        }
    }
}
