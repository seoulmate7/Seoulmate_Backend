package com.nexus.seoulmate.notification.domain.repository;

import com.nexus.seoulmate.notification.domain.LinkTargetType;
import com.nexus.seoulmate.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 사용자가 받은 알림을 최신순으로 (타입 필터 기준으로)
    @Query("""
    select n
    from Notification n
    where n.receiverId = :receiverId
        and n.targetType = :type
    """)
    Page<Notification> findByReceiverAndType(Long receiverId, LinkTargetType type, Pageable pageable);


    // 단일 알람 조회 (소유자 확인)
    @Query("""
    select n
    from Notification n
    where n.id = :id
        and n.receiverId = :receiverId
    """)
    Optional<Notification> findByIdAndReceiverId(Long id, Long receiverId);
}
