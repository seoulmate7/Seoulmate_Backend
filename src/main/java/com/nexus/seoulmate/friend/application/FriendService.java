package com.nexus.seoulmate.friend.application;

import com.nexus.seoulmate.friend.dto.FriendRequestDTO;

public interface FriendService {
    void sendFriendRequest(FriendRequestDTO.FriendRequestCreateDTO request);
}
