package com.nexus.seoulmate.chat.converter;

import com.nexus.seoulmate.chat.domain.entity.ChatRoom;
import com.nexus.seoulmate.chat.domain.entity.Message;
import com.nexus.seoulmate.chat.dto.ChatRoomDTO;
import com.nexus.seoulmate.chat.dto.MessageDTO;
import com.nexus.seoulmate.member.domain.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatConverter {

    public ChatRoomDTO.RoomSummary toRoomSummaryDirect(ChatRoom room,
                                                       Long myUserId,
                                                       List<ChatRoomDTO.Participant> participants) {
        return ChatRoomDTO.RoomSummary.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .roomImageUrl(room.getChatImage())
                .type(room.getType().name()) // "DIRECT"
                .myUserId(myUserId)
                .participants(participants)
                .build();
    }

    public ChatRoomDTO.RoomSummary toRoomSummaryGroup(ChatRoom room,
                                                      Long myUserId,
                                                      List<ChatRoomDTO.Participant> participants) {
        return ChatRoomDTO.RoomSummary.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .roomImageUrl(room.getChatImage())
                .type(room.getType().name()) // "GROUP"
                .myUserId(myUserId)
                .participants(participants)
                .build();
    }

    public ChatRoomDTO.RoomListItem toListItemGroup(ChatRoom room, Message latest, int unreadCount) {
        return ChatRoomDTO.RoomListItem.builder()
                .roomId(room.getId())
                .type(room.getType().name())
                .title(room.getTitle())
                .roomImageUrl(room.getChatImage())
                .partnerUserId(null)
                .lastMessageType(latest != null ? latest.getType().name() : null)
                .lastMessage(latest != null ? summarize(latest) : null)
                .lastMessageAt(latest != null ? latest.getCreatedAt() : null)
                .unreadCount(unreadCount)
                .build();
    }

    public ChatRoomDTO.RoomListItem toListItemDirect(ChatRoom room, Long partnerId, String partnerName, Message latest, int unreadCount) {
        return ChatRoomDTO.RoomListItem.builder()
                .roomId(room.getId())
                .type(room.getType().name())
                .title(room.getTitle())
                .roomImageUrl(room.getChatImage())
                .partnerUserId(partnerId)
                .lastMessageType(latest != null ? latest.getType().name() : null)
                .lastMessage(latest != null ? summarize(latest) : null)
                .lastMessageAt(latest != null ? latest.getCreatedAt() : null)
                .unreadCount(unreadCount)
                .build();
    }

    private String summarize(Message m) {
        return switch (m.getType()) {
            case TEXT -> {
                String c = m.getContent();
                yield (c != null && c.length() > 60) ? c.substring(0, 60) + "…" : c;
            }
            case FILE -> "[파일]";
            case SYS  -> m.getContent();
        };
    }

    public MessageDTO.Sent toSent(Message m, Member sender) {
        return MessageDTO.Sent.builder()
                .messageId(m.getId())
                .roomId(m.getRoomId())
                .senderId(m.getSenderId())
                .senderName(formatName(sender))
                .senderProfileUrl(sender.getProfileImage())
                .type(m.getType().name())
                .content(m.getContent())
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    public MessageDTO.MessageItem toMessageItem(Message m, Member sender, Long meId) {
        return MessageDTO.MessageItem.builder()
                .messageId(m.getId())
                .roomId(m.getRoomId())
                .senderId(m.getSenderId())
                .senderName(formatName(sender))
                .senderProfileImageUrl(sender.getProfileImage())
                .type(m.getType().name())
                .content(m.getContent())
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt() : LocalDateTime.now())
                .mine(m.getSenderId().equals(meId))
                .build();
    }

    public MessageDTO.Page toMessagePage(List<MessageDTO.MessageItem> items, Long nextCursor, boolean hasMore) {
        List<MessageDTO.MessageItem> asc = items.stream()
                .sorted((a, b) -> a.getMessageId().compareTo(b.getMessageId()))
                .collect(Collectors.toList());

        return MessageDTO.Page.builder()
                .items(asc)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    public ChatRoomDTO.Participant toParticipant(Member m, Long userId, String role, boolean me) {
        return ChatRoomDTO.Participant.builder()
                .userId(userId)
                .name(formatName(m))
                .profileImageUrl(m.getProfileImage())
                .role(role)
                .me(me)
                .build();
    }

    public ChatRoomDTO.RoomHeader toHeaderDirect(ChatRoom room, String partnerName,
                                                 List<ChatRoomDTO.Participant> participants) {
        return ChatRoomDTO.RoomHeader.builder()
                .roomId(room.getId())
                .type(room.getType().name())
                .title(partnerName)
                .roomImageUrl(room.getChatImage())
                .participants(participants)
                .build();
    }

    public ChatRoomDTO.RoomHeader toHeaderGroup(ChatRoom room,
                                                List<ChatRoomDTO.Participant> participants) {
        return ChatRoomDTO.RoomHeader.builder()
                .roomId(room.getId())
                .type(room.getType().name())
                .title(room.getTitle())
                .roomImageUrl(room.getChatImage())
                .participants(participants)
                .build();
    }

    public String formatName(Member member) {
        switch (member.getCountry()) {
            case KOREA:
            case CHINA:
            case JAPAN:
                return member.getLastName() + member.getFirstName();
            default:
                return member.getFirstName() + " " + member.getLastName();
        }
    }
}
