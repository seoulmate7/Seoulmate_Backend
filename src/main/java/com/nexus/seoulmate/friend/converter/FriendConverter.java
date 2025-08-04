package com.nexus.seoulmate.friend.converter;

import com.nexus.seoulmate.friend.domain.entity.FriendRequest;
import com.nexus.seoulmate.friend.domain.entity.FriendRequestStatus;
import com.nexus.seoulmate.friend.domain.entity.Friendship;
import com.nexus.seoulmate.friend.dto.FriendResponseDTO;
import com.nexus.seoulmate.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class FriendConverter {

    public FriendRequest toFriendRequest(Member sender, Member receiver) {
        return FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();
    }

    public Friendship toFriendship(FriendRequest friendRequest) {
        Member sender = friendRequest.getSender();
        Member receiver = friendRequest.getReceiver();

        Member userId1;
        Member userId2;

        if (sender.getUserId() < receiver.getUserId()) {
            userId1 = sender;
            userId2 = receiver;
        } else {
            userId1 = receiver;
            userId2 = sender;
        }

        return Friendship.builder()
                .userId1(userId1)
                .userId2(userId2)
                .chemistry(0)
                .build();
    }

    public FriendResponseDTO.FriendRequestListDTO toFriendRequestListDTO(FriendRequest request) {
        Member sender = request.getSender();

        return FriendResponseDTO.FriendRequestListDTO.builder()
                .requestId(request.getId())
                .senderId(sender.getUserId())
                .name(sender.getFirstName() + " " + sender.getLastName())
                .profileImage(sender.getProfileImage())
                .chemistry(0)
                .build();
    }

    public FriendResponseDTO.FriendListDTO toFriendListDTO(Member currentUser, Friendship friendship) {
        Member friend = friendship.getUserId1().getUserId().equals(currentUser.getUserId())
                ? friendship.getUserId2()
                : friendship.getUserId1();

        return FriendResponseDTO.FriendListDTO.builder()
                .userId(friend.getUserId())
                .name(friend.getFirstName() + " " + friend.getLastName())
                .profileImage(friend.getProfileImage())
                .chemistry(friendship.getChemistry())
                .build();
    }
}
