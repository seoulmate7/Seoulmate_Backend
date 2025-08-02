package com.nexus.seoulmate.friend.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.friend.application.FriendService;
import com.nexus.seoulmate.friend.dto.FriendRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
