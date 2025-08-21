package com.nexus.seoulmate.chat.api;

import com.nexus.seoulmate.chat.application.ChatService;
import com.nexus.seoulmate.chat.dto.ChatRoomDTO;
import com.nexus.seoulmate.chat.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    /**
     * 메시지 전송
     * 클라이언트 → SEND: /app/rooms/{roomId}/send
     * 서버 → (AFTER_COMMIT) Redis 발행 → RedisSubscriber가 /topic/room.{roomId} 로 브로드캐스트
     */
    @MessageMapping("/rooms/{roomId}/send")
    public void send(
            @DestinationVariable Long roomId,
            @Payload MessageDTO.SendRequest request,
            Principal principal
    ) {
        log.info("[WS] send enter roomId={}, payload={}", roomId, request);
        chatService.sendMessage(roomId, request);
        log.info("[WS] send exit roomId={}", roomId);
        // 주의: 실제 브로드캐스트는 AFTER_COMMIT 리스너 → Redis → Subscriber → /topic/room.{roomId}
    }

    /**
     * 읽음 처리(브로드캐스트)
     * 클라이언트 → SEND: /app/rooms/{roomId}/read (헤더: upToMessageId)
     * 서버 → 브로드캐스트: /topic/room.{roomId}.read
     */
    @MessageMapping("/rooms/{roomId}/read")
    public void markAsRead(
            @DestinationVariable Long roomId,
            @Header(name = "upToMessageId", required = false) Long upToMessageId
    ) {
        chatService.markAsRead(roomId, upToMessageId);
        var payload = new ReadEvent(roomId, upToMessageId);
        template.convertAndSend("/topic/room." + roomId + ".read", payload);
    }

    /**
     * 그룹방 합류(요청자 개인 응답)
     * 클라이언트 → SEND: /app/group/join (payload: GroupJoinRequest)
     * 서버 → /user/queue/group.joined
     */
    @MessageMapping("/group/join")
    @SendToUser("/queue/group.joined")
    public ChatRoomDTO.RoomSummary joinGroup(@Payload ChatRoomDTO.GroupJoinRequest req) {
        return chatService.joinGroupRoom(req);
    }

    /**
     * 방 헤더 조회(요청자 개인 응답)
     * 클라이언트 → SEND: /app/rooms/{roomId}/header
     * 서버 → /user/queue/room.header
     */
    @MessageMapping("/rooms/{roomId}/header")
    @SendToUser("/queue/room.header")
    public ChatRoomDTO.RoomHeader header(@DestinationVariable Long roomId) {
        return chatService.getRoomHeader(roomId);
    }

    /**
     * 메시지 페이지 조회(커서 기반, 요청자 개인 응답)
     * 클라이언트 → SEND: /app/rooms/{roomId}/messages (헤더: cursor, size)
     * 서버 → /user/queue/room.messages
     */
    @MessageMapping("/rooms/{roomId}/messages")
    @SendToUser("/queue/room.messages")
    public MessageDTO.Page messages(
            @DestinationVariable Long roomId,
            @Header(name = "cursor", required = false) Long cursor,
            @Header(name = "size", required = false) Integer size
    ) {
        int pageSize = (size == null ? 30 : size);
        return chatService.getMessages(roomId, cursor, pageSize);
    }

    /**
     * 1:1 방 생성(요청자 개인 응답)
     * 클라이언트 → SEND: /app/room/onetoone (payload: DirectCreateRequest)
     * 서버 → /user/queue/room.created
     */
    @MessageMapping("/room/onetoone")
    @SendToUser("/queue/room.created")
    public ChatRoomDTO.RoomSummary createDirect(@Payload ChatRoomDTO.DirectCreateRequest req) {
        return chatService.createDirectRoom(req);
    }

    /**
     * 그룹 방 생성(요청자 개인 응답)
     * 클라이언트 → SEND: /app/room/group (payload: GroupCreateRequest)
     * 서버 → /user/queue/room.created
     */
    @MessageMapping("/room/group")
    @SendToUser("/queue/room.created")
    public ChatRoomDTO.RoomSummary createGroup(@Payload ChatRoomDTO.GroupCreateRequest req) {
        return chatService.createGroupRoom(req);
    }

    /**
     * 내 방 목록 조회(요청자 개인 응답) — 실시간 목록 패널을 WS만으로도 갱신 가능
     * 클라이언트 → SEND: /app/rooms (헤더: type, page, size)
     * 서버 → /user/queue/rooms
     */
    @MessageMapping("/rooms")
    @SendToUser("/queue/rooms")
    public List<ChatRoomDTO.RoomListItem> myRooms(
            @Header(name = "type", required = false) String type,
            @Header(name = "page", required = false) Integer page,
            @Header(name = "size", required = false) Integer size
    ) {
        int p = page == null ? 0 : page;
        int s = size == null ? 20 : size;
        return chatService.getMyRooms(type, p, s);
    }

    public record ReadEvent(Long roomId, Long upToMessageId) {}
}
