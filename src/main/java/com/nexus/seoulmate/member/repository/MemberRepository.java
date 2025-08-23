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
    Member findByUserId(Long userId);
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m " +
           "LEFT JOIN FETCH m.languages " +
           "LEFT JOIN FETCH m.hobbies " +
           "WHERE m.email = :email")
    Optional<Member> findByEmailWithDetails(@Param("email") String email);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.languages WHERE m.userId = :userId")
    Optional<Member> findWithLanguagesById(@Param("userId") Long userId);

    Page<Member> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String first, String last, Pageable pageable);

    @Query("""
    SELECT m FROM Member m
    WHERE m.userId <> :currentUserId
      AND m.userId NOT IN (
        SELECT f.userId2.userId FROM Friendship f WHERE f.userId1.userId = :currentUserId
      )
      AND m.userId NOT IN (
        SELECT f.userId1.userId FROM Friendship f WHERE f.userId2.userId = :currentUserId
      )""")
    List<Member> findAllExcludingFriendsAndSelf(@Param("currentUserId") Long currentUserId);

    @Query("""
    SELECT m
    FROM Member m
    WHERE EXISTS (
      SELECT 1
      FROM Friendship f
      WHERE (f.userId1 = m AND f.userId2.userId = :currentId)
         OR (f.userId2 = m AND f.userId1.userId = :currentId)
    )
      AND (
           LOWER(m.firstName) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(m.lastName)  LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(CONCAT(m.lastName, m.firstName)) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(CONCAT(m.firstName, ' ', m.lastName)) LIKE LOWER(CONCAT('%', :q, '%'))
      )
    """)
    Page<Member> searchMyFriendsByName(@Param("currentId") Long currentId,
                                       @Param("q") String query,
                                       Pageable pageable);

    @Query("""
    SELECT m
    FROM Member m
    WHERE m.userId <> :currentId
      AND (
           LOWER(m.firstName) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(m.lastName)  LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(CONCAT(m.lastName, m.firstName)) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(CONCAT(m.firstName, ' ', m.lastName)) LIKE LOWER(CONCAT('%', :q, '%'))
      )
      AND NOT EXISTS (
        SELECT 1
        FROM Friendship f
        WHERE (f.userId1 = m AND f.userId2.userId = :currentId)
           OR (f.userId2 = m AND f.userId1.userId = :currentId)
      )
    """)
    Page<Member> searchNonFriendsByName(@Param("currentId") Long currentId,
                                        @Param("q") String query,
                                        Pageable pageable);


    // 프로필 이미지 조회하는 메서드
    @Query("select m.profileImage from Member m where m.userId = :userId")
    Optional<String> findProfileImageById(@Param("userId") Long userId);
}
