package com.nexus.seoulmate.chat.domain.repository;

import com.nexus.seoulmate.chat.domain.entity.ChatRoom;
import com.nexus.seoulmate.chat.domain.entity.ChatRoomMember;
import com.nexus.seoulmate.chat.domain.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
       select r from ChatRoom r
       join ChatRoomMember m on r.id = m.roomId
       left join Message msg on r.id = msg.roomId
       where m.userId = :userId
         and (:type is null or r.type = :type)
       group by r
       order by coalesce(max(msg.createdAt), r.createdAt) desc
    """)
    Page<ChatRoom> findMyRoomsOrderByLatestMessage(Long userId, RoomType type, Pageable pageable);

    Optional<ChatRoom> findByMeetingIdAndType(Long meetingId, RoomType type);

    @Query("""
select r from ChatRoom r
join ChatRoomMember m1 on r.id = m1.roomId
join ChatRoomMember m2 on r.id = m2.roomId
where r.type = 'DIRECT'
  and m1.userId = :userId1
  and m2.userId = :userId2
""")
    Optional<ChatRoom> findDirectRoomByUsers(@Param("userId1") Long userId1,
                                             @Param("userId2") Long userId2);
}
