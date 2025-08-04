package com.nexus.seoulmate.member.repository;

import com.nexus.seoulmate.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.languages WHERE m.userId = :userId")
    Optional<Member> findWithLanguagesById(@Param("userId") Long userId);
    Page<Member> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String first, String last, Pageable pageable);
}
