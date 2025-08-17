package com.nexus.seoulmate.notification.api.dto.response;

import com.nexus.seoulmate.notification.domain.LinkTargetType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationRes {
    private final Long id;
    private final String title;
    private final String message;
    private final String link;
    private final LinkTargetType linkTargetType;
    private final Long targetId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationRes(Long id,
                           String title,
                           String message,
                           String link,
                           LinkTargetType targetType,
                           Long targetId,
                           boolean isRead,
                           LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.link = link;
        this.linkTargetType = targetType;
        this.targetId = targetId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}
