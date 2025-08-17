package com.nexus.seoulmate.notification.api.dto;

import com.nexus.seoulmate.notification.domain.LinkTargetType;

import java.time.LocalDateTime;

public record NotificationPushDto(
        Long id,
        String title,
        String message,
        String link,
        LinkTargetType targetType,
        boolean isRead,
        LocalDateTime createdAt
) {
}
