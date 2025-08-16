package com.nexus.seoulmate.notification.api.dto;

import com.nexus.seoulmate.notification.domain.LinkTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotificationPushDto {

    private Long id;
    private String title;
    private String message;
    private String link;
    private LinkTargetType linkTargetType;
    private boolean isRead;
    private LocalDateTime createdAt;
}
