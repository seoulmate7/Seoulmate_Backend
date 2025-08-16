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
import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.*;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.notification.event.FriendRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendConverter friendConverter;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void sendFriendRequest(FriendRequestDTO.FriendRequestCreateDTO request) {

        Member currentUser = memberService.getCurrentUser();

        Member sender = memberRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        if (currentUser.getUserId().equals(request.getReceiverId())) {
            throw new CustomException(ErrorStatus.FRIEND_REQUEST_SELF);
        }

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new CustomException(ErrorStatus.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        FriendRequest friendRequest = friendConverter.toFriendRequest(sender, receiver);
        friendRequestRepository.save(friendRequest);

        eventPublisher.publishEvent(new FriendRequestedEvent(
                receiver.getUserId(),
                sender.getUserId(),
                sender.getFirstName()
        ));
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

            Member requester = friendRequest.getSender(); // 요청
            Member accepter = friendRequest.getReceiver(); // 수락

            eventPublisher.publishEvent(new FriendRequestedEvent(
                    requester.getUserId(),
                    accepter.getUserId(),
                    accepter.getFirstName()
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.FriendRequestListDTO> getFriendRequests() {
        Member currentUser = memberService.getCurrentUser();

        List<FriendRequest> requestList = friendRequestRepository.findBySenderAndStatus(currentUser, FriendRequestStatus.PENDING);

        return requestList.stream().map(friendConverter::toFriendRequestListDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.FriendListDTO> getFriends() {
        Member currentUser = memberService.getCurrentUser();
        List<Friendship> friendships = friendshipRepository.findByUser(currentUser);

        return friendships.stream()
                .map(f -> friendConverter.toFriendListDTO(currentUser, f))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FriendResponseDTO.FriendDetailDTO getFriendDetail(Long userId) {
        Member currentUser = memberService.getCurrentUser();

        Member targetUser = memberRepository.findWithLanguagesById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEMBER_NOT_FOUND));

        boolean isFriend = friendshipRepository.findFriendshipByUsers(currentUser, targetUser).isPresent();

        return friendConverter.toFriendDetailDTO(targetUser, isFriend);
    }

    @Override
    public void deleteFriend(Long userId) {
        Member currentUser = memberService.getCurrentUser();

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
        Member currentUser = memberService.getCurrentUser();

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

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.FriendRecommendationDTO> getLanguageBasedRecommendations() {
        Member currentUser = memberService.getCurrentUser();
        Map<String, Integer> myLanguages = currentUser.getLanguages().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));

        List<Member> candidates = memberRepository.findAllExcludingFriendsAndSelf(currentUser.getUserId());

        List<FriendResponseDTO.FriendRecommendationDTO> recommendations = new ArrayList<>();

        for (Member candidate : candidates) {
            Map<String, Integer> theirLanguages = candidate.getLanguages().entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
            List<FriendResponseDTO.FriendRecommendationDTO.MatchedLanguageDTO> matchedLanguages = new ArrayList<>();
            int matchedCount = 0;

            for (Map.Entry<String, Integer> myEntry : myLanguages.entrySet()) {
                String myLang = myEntry.getKey();
                int myLevel = myEntry.getValue();

                if (theirLanguages.containsKey(myLang)) {
                    int theirLevel = theirLanguages.get(myLang);
                    if (Math.abs(myLevel - theirLevel) <= 10) {
                        matchedCount++;
                        matchedLanguages.add(FriendResponseDTO.FriendRecommendationDTO.MatchedLanguageDTO.builder()
                                .languageName(myLang)
                                .myLevel(myLevel)
                                .theirLevel(theirLevel)
                                .build());
                    }
                }
            }

            if (!matchedLanguages.isEmpty()) {
                recommendations.add(
                        friendConverter.toFriendRecommendationDTO(candidate, matchedLanguages)
                );
            }
        }
        recommendations.sort(Comparator.comparingInt(FriendResponseDTO.FriendRecommendationDTO::getTotalMatchedLanguages).reversed());
        return recommendations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDTO.HobbyRecommendationDTO> getHobbyBasedRecommendations() {
        Member currentUser = memberService.getCurrentUser();

        var myHobbyIds = (currentUser.getHobbies() == null ? List.<Hobby>of() : currentUser.getHobbies())
                .stream()
                .map(Hobby::getHobbyId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Member> candidates = memberRepository.findAllExcludingFriendsAndSelf(currentUser.getUserId());

        List<FriendResponseDTO.HobbyRecommendationDTO> out = new ArrayList<>();

        for (Member candidate : candidates) {
            var hobbies = candidate.getHobbies();
            if (hobbies == null || hobbies.isEmpty()) continue;

            List<String> matchedHobbyNames = hobbies.stream()
                    .filter(h -> h.getHobbyId() != null && myHobbyIds.contains(h.getHobbyId()))
                    .map(Hobby::getHobbyName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (!matchedHobbyNames.isEmpty()) {
                out.add(friendConverter.toHobbyRecommendationDTO(candidate, matchedHobbyNames));
            }
        }

        out.sort(Comparator
                .comparingInt(FriendResponseDTO.HobbyRecommendationDTO::getTotalMatchedHobbies).reversed());

        return out;
    }
}
