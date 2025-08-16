package com.nexus.seoulmate.notification.event;

import com.nexus.seoulmate.notification.api.dto.NotificationPushDto;
import com.nexus.seoulmate.notification.application.NotificationPushService;
import com.nexus.seoulmate.notification.domain.LinkTargetType;
import com.nexus.seoulmate.notification.domain.Notification;
import com.nexus.seoulmate.notification.domain.repository.NotificationRepository;
import com.nexus.seoulmate.notification.support.NotificationTemplates;
import com.nexus.seoulmate.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationRepository notificationRepository;
    private final NotificationPushService notificationPushService;

    // 결제 성공 시 호스트에게 내 모임 참가 알림 (저장 후 즉시 푸시)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 트랜잭션 커밋 성공 후 실행
    public void onPaymentCaptured(PaymentCaptureEvent event) {
        if(event.status() != PaymentStatus.PAID) return;

        Notification n = new Notification().initWithTarget(
                event.hostId(),
                NotificationTemplates.meetingJoinedTitle(),
                NotificationTemplates.meetingJoinedContent(event.participantName()),
                LinkTargetType.MEETING,
                event.meetingId()

        );
        n = notificationRepository.save(n); // db 저장

        // 실시간 푸시 (빨간 점 표시 : isRead=false)
        notificationPushService.pushNew(n.getReceiverId(), toPush(n));
    }

    // 친구 신청 도착 (수신자에게 저장 후 푸시)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFriendRequested(FriendRequestedEvent event) {
        Notification n = new Notification().initWithTarget(
                event.receiverId(),
                NotificationTemplates.friendRequestedTitle(),
                NotificationTemplates.friendRequestedContent(event.senderName()),
                LinkTargetType.FRIEND,
                event.senderId()
        );
        n = notificationRepository.save(n);
        notificationPushService.pushNew(n.getReceiverId(), toPush(n));
    }

    // 친구 신청 수락 (요청자에게 저장 후 푸시)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFriendAccepted(FriendAcceptedEvent event) {
        Notification n = new Notification().initWithTarget(
                event.requesterId(),
                NotificationTemplates.friendAcceptedTitle(),
                NotificationTemplates.friendRequestedContent(event.acceptorName()),
                LinkTargetType.FRIEND,
                event.acceptorId()
        );
        n = notificationRepository.save(n);
        notificationPushService.pushNew(n.getReceiverId(), toPush(n));
    }

    private NotificationPushDto toPush(Notification n) {
        return new NotificationPushDto(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getLink(),
                n.getTargetType(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
