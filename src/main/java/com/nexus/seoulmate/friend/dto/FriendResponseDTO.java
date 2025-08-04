package com.nexus.seoulmate.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FriendResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendListDTO {
        private Long userId;
        private String name;
        private String profileImage;
        private int chemistry;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRequestListDTO {
        private Long requestId;
        private Long senderId;
        private String name;
        private String profileImage;
        private int chemistry;
    }

}
