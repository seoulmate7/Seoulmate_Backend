package com.nexus.seoulmate.member.repository;

import com.nexus.seoulmate.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.languages WHERE m.userId = :userId")
    Optional<Member> findWithLanguagesById(@Param("userId") Long userId);
    Page<Member> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String first, String last, Pageable pageable);
    @Query("""
    SELECT m FROM Member m 
    WHERE m.id <> :currentUserId 
      AND m.id NOT IN (
        SELECT f.userId2.id FROM Friendship f WHERE f.userId1.id = :currentUserId
      )
      AND m.id NOT IN (
        SELECT f.userId1.id FROM Friendship f WHERE f.userId2.id = :currentUserId
      )""")
    List<Member> findAllExcludingFriendsAndSelf(@Param("currentUserId") Long currentUserId);

}
