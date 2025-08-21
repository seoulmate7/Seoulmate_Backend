package com.nexus.seoulmate.chat.application;

import com.nexus.seoulmate.chat.dto.ChatRoomDTO;
import com.nexus.seoulmate.chat.dto.MessageDTO;

import java.util.List;

public interface ChatService {
    ChatRoomDTO.RoomSummary createDirectRoom(ChatRoomDTO.DirectCreateRequest req);
    ChatRoomDTO.RoomSummary createGroupRoom(ChatRoomDTO.GroupCreateRequest req);
    List<ChatRoomDTO.RoomListItem> getMyRooms(String type, int page, int size);
    MessageDTO.Sent sendMessage(Long roomId, MessageDTO.SendRequest req);
    MessageDTO.Page getMessages(Long roomId, Long cursor, int size);
    ChatRoomDTO.RoomHeader getRoomHeader(Long roomId);
    ChatRoomDTO.RoomSummary joinGroupRoom(ChatRoomDTO.GroupJoinRequest req);
    void markAsRead(Long roomId, Long upToMessageId);
}
