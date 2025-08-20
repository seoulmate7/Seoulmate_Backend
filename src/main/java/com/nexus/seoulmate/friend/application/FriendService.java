package com.nexus.seoulmate.friend.application;

import com.nexus.seoulmate.friend.dto.FriendRequestDTO;
import com.nexus.seoulmate.friend.dto.FriendResponseDTO;

import java.util.List;

public interface FriendService {
    void sendFriendRequest(FriendRequestDTO.FriendRequestCreateDTO request);
    void updateFriendRequest(Long requestId, FriendRequestDTO.FriendRequestUpdateDTO request);
    List<FriendResponseDTO.FriendRequestListDTO> getFriendRequests();
    List<FriendResponseDTO.FriendListDTO> getFriends();
    FriendResponseDTO.FriendDetailDTO getFriendDetail(Long userId);
    void deleteFriend(Long userId);
    List<FriendResponseDTO.FriendRecommendationDTO> getLanguageBasedRecommendations();
    List<FriendResponseDTO.HobbyRecommendationDTO> getHobbyBasedRecommendations();
    FriendResponseDTO.PagedFriendSearchResultDTO searchFriends(String query, int page, int size);
    FriendResponseDTO.PagedFriendListDTO searchAmongMyFriends(String query, int page, int size);

}
