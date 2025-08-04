package com.nexus.seoulmate.friend.domain.repository;

import com.nexus.seoulmate.friend.domain.entity.Friendship;
import com.nexus.seoulmate.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.userId1 " +
            "JOIN FETCH f.userId2 " +
            "WHERE f.userId1 = :member OR f.userId2 = :member")
    List<Friendship> findByUser(@Param("member") Member member);

}
