package com.nexus.seoulmate.chat.domain.repository;

import com.nexus.seoulmate.chat.domain.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

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
}
