package com.nexus.seoulmate.friend.domain.repository;

import com.nexus.seoulmate.friend.domain.entity.Friendship;
import com.nexus.seoulmate.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.userId1 " +
            "JOIN FETCH f.userId2 " +
            "WHERE f.userId1 = :member OR f.userId2 = :member")
    List<Friendship> findByUser(@Param("member") Member member);

    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.userId1 " +
            "JOIN FETCH f.userId2 " +
            "WHERE (f.userId1 = :user1 AND f.userId2 = :user2) OR (f.userId1 = :user2 AND f.userId2 = :user1)")
    Optional<Friendship> findFriendshipByUsers(@Param("user1") Member user1, @Param("user2") Member user2);
}
