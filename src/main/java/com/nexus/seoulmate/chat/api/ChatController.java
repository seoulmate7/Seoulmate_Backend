package com.nexus.seoulmate.chat.api;

import com.nexus.seoulmate.chat.application.ChatService;
import com.nexus.seoulmate.chat.dto.ChatRoomDTO;
import com.nexus.seoulmate.chat.dto.MessageDTO;
import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/room/onetoone")
    @Operation(
            summary = "1:1 채팅방 생성",
            description = "me ↔ partnerUserId 조합으로 DIRECT 방을 생성합니다."
    )
    public ResponseEntity<Response<ChatRoomDTO.RoomSummary>> createDirectRoom(
            @RequestBody ChatRoomDTO.DirectCreateRequest request
    ) {
        ChatRoomDTO.RoomSummary result = chatService.createDirectRoom(request);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_ROOM_CREATED, result));
    }

    @PostMapping("/room/group")
    @Operation(
            summary = "그룹 채팅방 생성",
            description = "현재 로그인 사용자를 OWNER로, 전달된 사용자들을 PARTICIPANT로 추가하여 GROUP 방을 생성합니다."
    )
    public ResponseEntity<Response<ChatRoomDTO.RoomSummary>> createGroupRoom(
            @RequestBody ChatRoomDTO.GroupCreateRequest request
    ) {
        var result = chatService.createGroupRoom(request);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_ROOM_CREATED, result));
    }

    @GetMapping("/rooms")
    @Operation(
            summary = "내 채팅방 목록 조회",
            description = "현재 로그인 사용자가 속한 채팅방 목록을 반환합니다. type은 미지정(전체) 또는 DIRECT | GROUP 만 허용합니다. page/size는 페이지네이션."
    )
    public ResponseEntity<Response<List<ChatRoomDTO.RoomListItem>>> getMyRooms(
            @Parameter(description = "DIRECT | GROUP") @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var results = chatService.getMyRooms(type, page, size);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_ROOMS_FETCHED, results));
    }

    @PostMapping("/rooms/{roomId}/messages")
    @Operation(
            summary = "메시지 전송",
            description = "DB에 저장 후 Redis로 발행하여 STOMP로 브로드캐스트합니다. (MessageDTO.Sent 단일 DTO)"
    )
    public ResponseEntity<Response<MessageDTO.Sent>> sendMessage(
            @PathVariable Long roomId,
            @RequestBody MessageDTO.SendRequest request,
            Principal principal
    ) {
        var result = chatService.sendMessage(roomId, request,principal);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_MESSAGE_SENT, result));
    }

    @GetMapping("/rooms/{roomId}/messages")
    @Operation(
            summary = "채팅 메시지 조회 (커서 기반)",
            description = "cursor 미지정 시 최신 N개, cursor 제공 시 해당 id보다 과거 메시지를 size만큼 반환합니다. 반환은 오래된→최신 순으로 정렬됩니다."
    )
    public ResponseEntity<Response<MessageDTO.Page>> getMessages(
            @PathVariable Long roomId,
            @Parameter(description = "이 id보다 오래된 메시지(과거) 조회") @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size
    ) {
        var page = chatService.getMessages(roomId, cursor, size);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_MESSAGES_FETCHED, page));
    }

    @GetMapping("/rooms/{roomId}/header")
    @Operation(
            summary = "채팅방 헤더 정보 조회",
            description = "DIRECT는 상대 이름을 title로, GROUP은 방 제목과 함께 참여자 전원의 이름/프로필 이미지를 반환합니다."
    )
    public ResponseEntity<Response<ChatRoomDTO.RoomHeader>> getRoomHeader(@PathVariable Long roomId) {
        var header = chatService.getRoomHeader(roomId);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_ROOM_FETCHED, header));
    }

    @PostMapping("/group/join")
    @Operation(
            summary = "사설모임 그룹 채팅 합류",
            description = "meetingId로 연결된 그룹 채팅방에 현재 사용자를 멤버로 추가합니다. 이미 멤버인 경우 idempotent하게 방 요약을 반환합니다."
    )
    public ResponseEntity<Response<ChatRoomDTO.RoomSummary>> joinGroup(
            @RequestBody ChatRoomDTO.GroupJoinRequest req
    ) {
        ChatRoomDTO.RoomSummary summary = chatService.joinGroupRoom(req);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_ROOM_JOINED, summary));
    }

    @PatchMapping("/rooms/{roomId}/read")
    @Operation(
            summary = "읽음 처리",
            description = "방에 들어갔거나 메시지를 끝까지 읽었을 때 호출. upToMessageId 미전달 시 최신 메시지까지 읽음으로 처리."
    )
    public ResponseEntity<Response<Void>> markAsRead(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long upToMessageId
    ) {
        chatService.markAsRead(roomId, upToMessageId);
        return ResponseEntity.ok(Response.success(SuccessStatus.CHAT_MESSAGES_MARKED_AS_READ, null));
    }

}
