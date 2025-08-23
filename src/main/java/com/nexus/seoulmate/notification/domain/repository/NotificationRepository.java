package com.nexus.seoulmate.notification.domain.repository;

import com.nexus.seoulmate.notification.domain.LinkTargetType;
import com.nexus.seoulmate.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
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

    @Query(value = """
            select m.profile_image
            from orders o
            join member m on m.user_id = o.user_id
            join payment p on p.order_id = o.order_id
            where o.meeting_id = :meetingId
              and o.status = 'PAID'
              and p.status = 'PAID'
            order by ABS(TIMESTAMPDIFF(SECOND, COALESCE(p.paid_at, o.create_at), :before)) ASC,
                     COALESCE(p.paid_at, o.create_at) DESC
            limit 1
            """, nativeQuery = true)
    Optional<String> findLatestPaidParticipantImageBefore(Long meetingId, LocalDateTime before);
}
