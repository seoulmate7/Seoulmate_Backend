package com.nexus.seoulmate.chat.domain.repository;

import com.nexus.seoulmate.chat.domain.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    @Query("select (count(m) > 0) from ChatRoomMember m where m.roomId = :roomId and m.userId = :userId")
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    @Query("""
        select m from ChatRoomMember m
        where m.roomId in :roomIds
    """)
    List<ChatRoomMember> findByRoomIdIn(Collection<Long> roomIds);

    @Query("select m from ChatRoomMember m where m.roomId = :roomId")
    List<ChatRoomMember> findByRoomId(Long roomId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update ChatRoomMember crm
       set crm.lastReadMessageId =
           case
             when crm.lastReadMessageId is null then :msgId
             when crm.lastReadMessageId < :msgId then :msgId
             else crm.lastReadMessageId
           end
     where crm.roomId = :roomId and crm.userId = :userId
    """)
    int updateLastRead(@Param("roomId") Long roomId,
                       @Param("userId") Long userId,
                       @Param("msgId") Long msgId);

}