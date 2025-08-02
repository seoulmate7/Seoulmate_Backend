package com.nexus.seoulmate.friend.domain.repository;

import com.nexus.seoulmate.friend.domain.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
}
