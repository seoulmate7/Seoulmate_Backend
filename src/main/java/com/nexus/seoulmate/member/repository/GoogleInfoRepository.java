package com.nexus.seoulmate.member.repository;

import com.nexus.seoulmate.member.domain.GoogleInfo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleInfoRepository extends JpaRepository<GoogleInfo, Long> {
    Optional<GoogleInfo> findByGoogleId(String googleId);
    Optional<GoogleInfo> findBySessionId(String sessionId);
}
