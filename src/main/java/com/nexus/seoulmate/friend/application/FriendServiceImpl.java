package com.nexus.seoulmate.friend.application;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.friend.converter.FriendConverter;
import com.nexus.seoulmate.friend.domain.entity.FriendRequest;
import com.nexus.seoulmate.friend.domain.entity.FriendRequestStatus;
import com.nexus.seoulmate.friend.domain.entity.Friendship;
import com.nexus.seoulmate.friend.domain.repository.FriendRequestRepository;
import com.nexus.seoulmate.friend.domain.repository.FriendshipRepository;
import com.nexus.seoulmate.friend.dto.FriendRequestDTO;
import com.nexus.seoulmate.friend.dto.FriendResponseDTO;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.*;
import com.nexus.seoulmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendConverter friendConverter;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void sendFriendRequest(FriendRequestDTO.FriendRequestCreateDTO request) {

        Long senderId = 1L; // 임의로 로그인된 사용자 지정

        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        if (senderId.equals(request.getReceiverId())) {
            throw new CustomException(ErrorStatus.FRIEND_REQUEST_SELF);
        }

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new CustomException(ErrorStatus.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        FriendRequest friendRequest = friendConverter.toFriendRequest(sender, receiver);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    @Transactional
    public void updateFriendRequest(Long requestId, FriendRequestDTO.FriendRequestUpdateDTO request) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));

        if (!friendRequest.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new CustomException(ErrorStatus.FRIEND_REQUEST_ALREADY_HANDLED);
        }

        friendRequest.updateStatus(request.getStatus());

        if (request.getStatus() == FriendRequestStatus.APPROVED) {
            Friendship friendship = friendConverter.toFriendship(friendRequest);
            friendshipRepository.save(friendship);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.FriendRequestListDTO> getFriendRequests() {
        Member currentUser = Member.builder() //임시 로그인 사용자
                .userId(1L)
                .email("dummyuser@seoulmate.com")
                .password("dummy")
                .firstName("Dummy")
                .lastName("User")
                .DOB(LocalDate.of(2000, 1, 1))
                .country(Countries.KOREA)
                .bio("더미 사용자입니다.")
                .profileImage("https://cdn.seoulmate.com/profile/dummy.png")
                .languages(Map.of("ENGLISH", 3))
                .hobbies(new ArrayList<>())
                .univCertificate("dummy_cert.png")
                .univ(University.SUNGSIL)
                .isVerified(VerificationStatus.VERIFIED)
                .isDeleted(false)
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .userStatus(UserStatus.ACTIVE)
                .build();

        List<FriendRequest> requestList = friendRequestRepository.findByReceiverAndStatus(currentUser, FriendRequestStatus.PENDING);

        return requestList.stream().map(friendConverter::toFriendRequestListDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.FriendListDTO> getFriends() {
        Member currentUser = Member.builder() //임시 로그인 사용자
                .userId(1L)
                .email("dummyuser@seoulmate.com")
                .password("dummy")
                .firstName("Dummy")
                .lastName("User")
                .DOB(LocalDate.of(2000, 1, 1))
                .country(Countries.KOREA)
                .bio("더미 사용자입니다.")
                .profileImage("https://cdn.seoulmate.com/profile/dummy.png")
                .languages(Map.of("ENGLISH", 3))
                .hobbies(new ArrayList<>())
                .univCertificate("dummy_cert.png")
                .univ(University.SUNGSIL)
                .isVerified(VerificationStatus.VERIFIED)
                .isDeleted(false)
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .userStatus(UserStatus.ACTIVE)
                .build();

        List<Friendship> friendships = friendshipRepository.findByUser(currentUser);

        return friendships.stream()
                .map(f -> friendConverter.toFriendListDTO(currentUser, f))
                .collect(Collectors.toList());
    }
}
