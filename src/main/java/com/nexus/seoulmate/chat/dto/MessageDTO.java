package com.nexus.seoulmate.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nexus.seoulmate.chat.domain.entity.MsgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendRequest {
        private MsgType type;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Sent {
        private Long messageId;
        private Long roomId;
        private Long senderId;
        private String senderName;
        private String type;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageItem {
        private Long messageId;
        private Long roomId;
        private Long senderId;
        private String senderName;
        private String senderProfileImageUrl;
        private String type;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        private boolean mine;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Page {
        private List<MessageItem> items;
        private Long nextCursor;
        private boolean hasMore;
    }

}
