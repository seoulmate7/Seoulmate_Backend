package com.nexus.seoulmate.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class FriendResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendListDTO {
        private Long userId;
        private String name;
        private String profileImage;
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
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendDetailDTO {
        private Long userId;
        private String name;
        private String profileImage;
        private String bio;
        private String university;
        private int age;
        private String country;
        private Map<String, Integer> languageLevels;
        private boolean isFriend;
        private List<String> hobbyList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendSearchResultDTO {
        private Long userId;
        private String name;
        private String profileImage;
    }
}
