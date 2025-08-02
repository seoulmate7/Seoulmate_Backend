package com.nexus.seoulmate.friend.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.friend.application.FriendService;
import com.nexus.seoulmate.friend.dto.FriendRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendController {

    private final FriendService friendRequestService;

    @PostMapping("/requests")
    @Operation(
            summary = "친구 요청 전송",
            description = "로그인한 사용자가 특정 사용자에게 친구 요청을 전송합니다."
    )
    public ResponseEntity<Response<Void>> sendFriendRequest(
            @RequestBody FriendRequestDTO.FriendRequestCreateDTO request
    ) {
        friendRequestService.sendFriendRequest(request);
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_REQUEST_SENT,null));
    }

    @PatchMapping("/requests/{requestId}")
    @Operation(
            summary = "친구 요청 수락 혹은 거절",
            description = "요청 ID에 해당하는 친구 요청을 수락하거나 거절합니다. 요청자는 로그인된 사용자이며, 요청 상태는 APPROVED 또는 REJECTED로 변경됩니다."
    )
    public ResponseEntity<Response<Void>> updateFriendRequest(
            @PathVariable Long requestId,
            @RequestBody FriendRequestDTO.FriendRequestUpdateDTO request
    ){
        friendRequestService.updateFriendRequest(requestId, request);
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_REQUEST_UPDATED, null));
    }
}
