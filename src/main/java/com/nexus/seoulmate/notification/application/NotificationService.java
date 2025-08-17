package com.nexus.seoulmate.notification.application;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.notification.api.dto.response.NotificationRes;
import com.nexus.seoulmate.notification.domain.LinkTargetType;
import com.nexus.seoulmate.notification.domain.Notification;
import com.nexus.seoulmate.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPushService notificationPushService;

    // 내 알림 목록 (최신순)
    @Transactional(readOnly = true)
    public Page<NotificationRes> findAll(Long receiverId, LinkTargetType type, Pageable pageable) {
        LinkTargetType linkTargetType = (type == null) ? LinkTargetType.MEETING : type;
        return notificationRepository.findByReceiverAndType(receiverId, linkTargetType, pageable)
                .map(n -> new NotificationRes(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getLink(),
                        n.getTargetType(),
                        n.getTargetId(),
                        n.isRead(),
                        n.getCreatedAt()

                ));
    }

    // 읽음 처리 + 다른 탭 이벤트 동기화
    @Transactional
    public void markRead(Long receiverId, Long notificationId) {
        // 알림 존재 확인
        Notification n = notificationRepository.findByIdAndReceiverId(notificationId, receiverId)
                .orElseThrow(() -> new CustomException(ErrorStatus.NOTIFICATION_NOT_FOUND));
        n.markRead();

        // 같은 유저가 연 다른 탭에도 읽음 처리
        notificationPushService.pushRead(receiverId, notificationId);
    }

}
