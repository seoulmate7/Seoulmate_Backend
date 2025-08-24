package com.nexus.seoulmate.friend.domain.repository;

import com.nexus.seoulmate.friend.domain.entity.FriendRequest;
import com.nexus.seoulmate.friend.domain.entity.FriendRequestStatus;
import com.nexus.seoulmate.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsBySenderAndReceiver(Member sender, Member receiver);
    List<FriendRequest> findByReceiverAndStatus(Member receiver, FriendRequestStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           DELETE FROM FriendRequest fr
           WHERE (fr.sender = :u1 AND fr.receiver = :u2)
              OR (fr.sender = :u2 AND fr.receiver = :u1)
           """)
    void deleteAllBetweenUsers(@Param("u1") Member u1, @Param("u2") Member u2);
}
