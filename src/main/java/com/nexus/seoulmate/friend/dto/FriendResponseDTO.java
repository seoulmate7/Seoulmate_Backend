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
        private List<String> hobbyList;
        private String relation;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRecommendationDTO {
        private Long userId;
        private String name;
        private String profileImage;
        private List<MatchedLanguageDTO> matchedLanguages;
        private int totalMatchedLanguages;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MatchedLanguageDTO {
            private String languageName;
            private int myLevel;
            private int theirLevel;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HobbyRecommendationDTO {
        private Long userId;
        private String name;
        private String profileImage;
        private List<String> matchedHobbies;
        private int totalMatchedHobbies;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedFriendSearchResultDTO {
        private List<FriendSearchResultDTO> content;
        private boolean hasNext;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedFriendListDTO {
        private List<FriendListDTO> content;
        private boolean hasNext;
    }

}
