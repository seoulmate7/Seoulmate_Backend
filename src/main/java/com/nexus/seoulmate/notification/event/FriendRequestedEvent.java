package com.nexus.seoulmate.notification.event;

public record FriendRequestedEvent(
        Long receiverId,
        Long senderId,
        String senderName
) {
}
