package com.nexus.seoulmate.chat.domain.repository;

import com.nexus.seoulmate.chat.domain.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository <Message,Long> {
    @Query("""
        select m from Message m
        where m.roomId in :roomIds
          and m.id in (
            select max(m2.id) from Message m2
            where m2.roomId in :roomIds
            group by m2.roomId
          )
    """)
    List<Message> findLatestByRoomIds(Collection<Long> roomIds);

    @Query("""
        select m from Message m
        where m.roomId = :roomId
          and (:cursorId is null or m.id < :cursorId)
        order by m.id desc
    """)
    List<Message> findRecent(@Param("roomId") Long roomId,
                             @Param("cursorId") Long cursorId,
                             Pageable pageable);

    boolean existsByRoomIdAndIdLessThan(Long roomId, Long id);

    Optional<Message> findTopByRoomIdOrderByIdDesc(Long roomId);

    @Query("""
    select m.roomId as roomId, count(m) as unread
    from Message m
      join ChatRoomMember crm
        on crm.roomId = m.roomId and crm.userId = :meId
    where m.roomId in :roomIds
      and m.id > coalesce(crm.lastReadMessageId, 0)
      and m.senderId <> :meId
    group by m.roomId
    """)
    List<UnreadCountView> findUnreadCounts(@Param("meId") Long meId,
                                           @Param("roomIds") Collection<Long> roomIds);

    interface UnreadCountView {
        Long getRoomId();
        Long getUnread();
    }

}
