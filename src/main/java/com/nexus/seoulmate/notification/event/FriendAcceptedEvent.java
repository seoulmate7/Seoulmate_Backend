package com.nexus.seoulmate.notification.event;

public record FriendAcceptedEvent(
        Long requesterId,
        Long acceptorId,
        String acceptorName,
        String acceptorImageUrl
) {
}
