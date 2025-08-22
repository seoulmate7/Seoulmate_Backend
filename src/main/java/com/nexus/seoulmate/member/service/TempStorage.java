package com.nexus.seoulmate.member.service;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.global.status.ErrorStatus;
import com.nexus.seoulmate.member.domain.enums.*;
import com.nexus.seoulmate.member.dto.signup.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TempStorage {

    private final RedisTemplate<String, Object> redisTemplate;
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

        redisTemplate.opsForHash().put(googleId, "languages", dto.getLanguages());
    }

    // 4. 취미 저장
    public void save(HobbyRequest dto, String googleId) {
        if (dto == null || dto.getHobbies() == null) {
            throw new CustomException(ErrorStatus.NULL_PARAMETER);
        }
        // 로그 추가: Redis에 저장하기 직전 값 확인
        System.out.println("로그: Redis에 저장될 구글 ID = " + googleId);
        System.out.println("로그: Redis에 저장될 취미 리스트 = " + dto.getHobbies());

        redisTemplate.opsForHash().put(googleId, "hobbies", dto.getHobbies());

        System.out.println("로그: Redis에 취미 정보 저장 완료.");
    }

    // 5. 학교 인증 신청
    public void save(UnivAuthDto dto, String googleId) {
        Map<String, Object> map = Map.of(
                "univ", dto.getUniversity(),
                "univCertificate", dto.getUnivCertificateUrl(),
                "verificationStatus", VerificationStatus.SUBMITTED
        );
        redisTemplate.opsForHash().putAll(googleId, map);
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
                    country = Countries.fromDisplayName(countryStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 country 값: " + countryStr);
                }
            }

            String bio = (String) raw.get("bio");
            String profileImage = (String) raw.get("profileImageUrl");  // profileImage -> profileImageUrl로 변경

            List<String> hobbies = (List<String>) raw.get("hobbies");;

            String univCertificate = (String) raw.get("univCertificate");

            University univ = null;
            Object univObj = raw.get("univ");
            if (univObj instanceof University) {
                univ = (University) univObj;
            } else if (univObj instanceof String univStr) {
                try {
                    univ = University.fromDisplayName(univStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("잘못된 university 값: " + univStr);
                }
            }

            Map<Languages, Integer> languages = null;
            Object langObj = raw.get("languages");
            if (langObj instanceof Map<?, ?> langMap) {
                try {
                    // Redis에서 가져온 Map을 새로운 Map으로 변환
                    Map<Languages, Integer> convertedLanguages = new HashMap<>();
                    for (Map.Entry<?, ?> entry : langMap.entrySet()) {
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (key instanceof String keyStr && value instanceof Integer intValue) {
                            try {
                                Languages language = Languages.fromDisplayName(keyStr);
                                convertedLanguages.put(language, intValue);
                            } catch (Exception e) {
                                System.out.println("잘못된 language 값: " + keyStr);
                            }
                        }
                    }
                    languages = convertedLanguages;
                } catch (Exception e) {
                    System.out.println("languages 변환 실패: " + e.getMessage());
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
