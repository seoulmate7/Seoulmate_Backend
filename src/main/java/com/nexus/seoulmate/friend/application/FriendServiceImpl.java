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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Member currentUser = getCurrentLoginMember();

        List<FriendRequest> requestList = friendRequestRepository.findByReceiverAndStatus(currentUser, FriendRequestStatus.PENDING);

        return requestList.stream().map(friendConverter::toFriendRequestListDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.FriendListDTO> getFriends() {
        Member currentUser = getCurrentLoginMember();
        List<Friendship> friendships = friendshipRepository.findByUser(currentUser);

        return friendships.stream()
                .map(f -> friendConverter.toFriendListDTO(currentUser, f))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FriendResponseDTO.FriendDetailDTO getFriendDetail(Long userId) {
        Member currentUser = getCurrentLoginMember();

        Member targetUser = memberRepository.findWithLanguagesById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        boolean isFriend = friendshipRepository.findFriendshipByUsers(currentUser, targetUser).isPresent();

        return friendConverter.toFriendDetailDTO(targetUser, isFriend);
    }

    @Override
    public void deleteFriend(Long userId) {
        Member currentUser = getCurrentLoginMember();

        Member targetUser = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Friendship friendship = friendshipRepository
                .findFriendshipByUsers(currentUser, targetUser)
                .orElseThrow(() -> new CustomException(ErrorStatus.FRIEND_RELATION_NOT_FOUND));

        friendshipRepository.delete(friendship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.FriendSearchResultDTO> searchFriends(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Member currentUser = getCurrentLoginMember();

        List<Friendship> friendships = friendshipRepository.findByUser(currentUser);
        List<Long> friendIds = friendships.stream()
                .map(f -> {
                    Member user1 = f.getUserId1();
                    Member user2 = f.getUserId2();
                    return user1.getUserId().equals(currentUser.getUserId()) ? user2.getUserId() : user1.getUserId();
                })
                .toList();

        Page<Member> matched = memberRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, pageable);

        return matched.stream()
                .filter(member -> !member.getUserId().equals(currentUser.getUserId()))
                .filter(member -> !friendIds.contains(member.getUserId()))
                .map(friendConverter::toFriendSearchResultDTO)
                .collect(Collectors.toList());
    }

    private Member getCurrentLoginMember() {
        return Member.builder()
                .userId(1L) // 테스트용 ID
                .email("dummy@seoulmate.com")
                .firstName("Dummy")
                .lastName("User")
                .DOB(LocalDate.of(2000, 1, 1))
                .country(Countries.KOREA)
                .bio("더미 사용자입니다.")
                .profileImage("https://cdn.seoulmate.com/profile/dummy.png")
                .languages(Map.of("Korean", 5, "English", 3))
                .hobbies(new ArrayList<>())
                .univCertificate("dummy_cert.png")
                .univ(University.SUNGSIL)
                .isVerified(VerificationStatus.VERIFIED)
                .isDeleted(false)
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .userStatus(UserStatus.ACTIVE)
                .password("oauth2")
                .build();
    }
}
