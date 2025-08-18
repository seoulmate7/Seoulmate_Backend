package com.nexus.seoulmate.friend.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.friend.application.FriendService;
import com.nexus.seoulmate.friend.dto.FriendRequestDTO;
import com.nexus.seoulmate.friend.dto.FriendResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Validated
@Tag(name = "Friend", description = "친구 관련 API")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/requests")
    @Operation(
            summary = "친구 요청 전송",
            description = "로그인한 사용자가 특정 사용자에게 친구 요청을 전송합니다."
    )
    public ResponseEntity<Response<Void>> sendFriendRequest(
            @RequestBody FriendRequestDTO.FriendRequestCreateDTO request
    ) {
        friendService.sendFriendRequest(request);
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
        friendService.updateFriendRequest(requestId, request);
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_REQUEST_UPDATED, null));
    }

    @GetMapping("/requests")
    @Operation(
            summary = "친구 요청 목록 조회",
            description = "내가 받은 친구 요청 목록을 조회합니다.")
    public ResponseEntity<Response<List<FriendResponseDTO.FriendRequestListDTO>>> getFriendRequests() {
        List<FriendResponseDTO.FriendRequestListDTO> responses = friendService.getFriendRequests();
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_REQUEST_LIST_FETCHED, responses));
    }

    @GetMapping
    @Operation(
            summary = "내 친구 목록 조회",
            description = "로그인한 사용자의 친구 목록을 조회합니다.")
    public ResponseEntity<Response<List<FriendResponseDTO.FriendListDTO>>> getFriends() {
        List<FriendResponseDTO.FriendListDTO> responses = friendService.getFriends();
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_LIST_FETCHED, responses));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 상세 정보 조회", description = "특정 사용자의 프로필 정보를 조회합니다.")
    public ResponseEntity<Response<FriendResponseDTO.FriendDetailDTO>> getFriendDetail(
            @PathVariable Long userId
    ) {
        FriendResponseDTO.FriendDetailDTO response = friendService.getFriendDetail(userId);
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_DETAIL_FETCHED, response));
    }

    @DeleteMapping("/{userId}")
    @Operation(
            summary = "친구 삭제",
            description = "특정 사용자와의 친구 관계를 삭제합니다."
    )
    public ResponseEntity<Response<Void>> deleteFriend(@PathVariable Long userId) {
        friendService.deleteFriend(userId);
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_DELETED, null));
    }

    @GetMapping("/search")
    @Operation(
            summary = "친구 검색",
            description = "키워드를 통해 친구가 아닌 사용자를 검색합니다."
    )
    public ResponseEntity<Response<List<FriendResponseDTO.FriendSearchResultDTO>>> searchFriends(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        List<FriendResponseDTO.FriendSearchResultDTO> results = friendService.searchFriends(query, page, size);
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_SEARCH_RESULT_FETCHED, results));
    }

    @GetMapping("/recommendations/language")
    @Operation(
            summary = "언어 실력 기반 추천 친구 조회",
            description = "사용자와 언어 실력이 유사한 사람들을 친구로 추천합니다. (각 언어별 ±10 수준 이내)"
    )
    public ResponseEntity<Response<List<FriendResponseDTO.FriendRecommendationDTO>>> getLanguageBasedRecommendations() {
        List<FriendResponseDTO.FriendRecommendationDTO> results = friendService.getLanguageBasedRecommendations();
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_RECOMMENDATION_FETCHED, results));
    }

    @GetMapping("/recommendations/hobby")
    @Operation(
            summary = "관심사(취미) 기반 추천 친구 조회",
            description = "내 취미와 많이 겹치는 사용자 순으로 추천합니다."
    )
    public ResponseEntity<Response<List<FriendResponseDTO.HobbyRecommendationDTO>>> getHobbyBasedRecommendations() {
        List<FriendResponseDTO.HobbyRecommendationDTO> results = friendService.getHobbyBasedRecommendations();
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_RECOMMENDATION_FETCHED, results));
    }

    @GetMapping("/search/my")
    @Operation(
            summary = "내 친구 중 검색",
            description = "내 친구들 중에서 이름(first/last)에 키워드가 포함된 사용자만 페이징 반환합니다."
    )
    public ResponseEntity<Response<List<FriendResponseDTO.FriendListDTO>>> searchAmongMyFriends(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        var responses = friendService.searchAmongMyFriends(query, page, size);
        return ResponseEntity.ok(Response.success(SuccessStatus.FRIEND_LIST_FETCHED, responses));
    }

}
