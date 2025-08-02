package com.nexus.seoulmate.friend.dto;

import com.nexus.seoulmate.friend.domain.entity.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FriendRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRequestCreateDTO {
        private Long receiverId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRequestUpdateDTO {
        private FriendRequestStatus status;
    }
}
