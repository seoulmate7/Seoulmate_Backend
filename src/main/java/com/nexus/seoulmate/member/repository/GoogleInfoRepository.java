package com.nexus.seoulmate.member.repository;

import com.nexus.seoulmate.member.domain.GoogleInfo;
import com.nexus.seoulmate.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleInfoRepository extends JpaRepository<GoogleInfo, Long> {
    Optional<GoogleInfo> findBySessionId(String sessionId);
    Optional<GoogleInfo> findByUserId(Member userId);
}
