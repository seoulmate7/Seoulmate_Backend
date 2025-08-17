package com.nexus.seoulmate.notification.api.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationReadRes {
    private final Long id;
    private final boolean isRead;
    private final LocalDateTime updatedAt;

    public NotificationReadRes(Long id, boolean isRead, LocalDateTime updatedAt) {
        this.id = id;
        this.isRead = isRead;
        this.updatedAt = updatedAt;
    }
}
