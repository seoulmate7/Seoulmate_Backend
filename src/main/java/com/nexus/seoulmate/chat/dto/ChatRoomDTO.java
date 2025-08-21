package com.nexus.seoulmate.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectCreateRequest {
        private Long partnerUserId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomSummary {
        private Long roomId;
        private String title;
        private String roomImageUrl;
        private String type; // "DIRECT" | "GROUP"
        private Long myUserId;

        private List<Participant> participants;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupCreateRequest {
        private Long meetingId;
        private List<Long> memberUserIds;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomListItem {
        private Long roomId;
        private String type;
        private String title;
        private String roomImageUrl;

        // DIRECT 전용
        private Long partnerUserId;       // 그룹이면 null

        private String lastMessageType;
        private String lastMessage;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime lastMessageAt;

        private int unreadCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Participant {
        private Long userId;
        private String name;
        private String profileImageUrl;
        private String role;
        private boolean me;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomHeader {
        private Long roomId;
        private String type;
        private String title;
        private String roomImageUrl;
        private List<Participant> participants;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupJoinRequest {
        private Long meetingId;
    }
}
