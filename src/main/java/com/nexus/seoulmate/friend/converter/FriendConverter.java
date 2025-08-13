package com.nexus.seoulmate.friend.converter;

import com.nexus.seoulmate.friend.domain.entity.FriendRequest;
import com.nexus.seoulmate.friend.domain.entity.FriendRequestStatus;
import com.nexus.seoulmate.friend.domain.entity.Friendship;
import com.nexus.seoulmate.friend.dto.FriendResponseDTO;
import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.Languages;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nexus.seoulmate.member.domain.enums.Languages.*;

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
                .build();
    }

    public FriendResponseDTO.FriendRequestListDTO toFriendRequestListDTO(FriendRequest request) {
        Member sender = request.getSender();

        return FriendResponseDTO.FriendRequestListDTO.builder()
                .requestId(request.getId())
                .senderId(sender.getUserId())
                .name(formatName(sender))
                .profileImage(sender.getProfileImage())
                .build();
    }

    public FriendResponseDTO.FriendListDTO toFriendListDTO(Member currentUser, Friendship friendship) {
        Member friend = friendship.getUserId1().getUserId().equals(currentUser.getUserId())
                ? friendship.getUserId2()
                : friendship.getUserId1();

        return FriendResponseDTO.FriendListDTO.builder()
                .userId(friend.getUserId())
                .name(formatName(friend))
                .profileImage(friend.getProfileImage())
                .build();
    }

    public FriendResponseDTO.FriendDetailDTO toFriendDetailDTO(Member member, boolean isFriend) {
        List<String> hobbyNames = member.getHobbies().stream()
                .map(Hobby::getHobbyName)
                .collect(Collectors.toList());

        Languages nativeLang = switch (member.getCountry()) {
            case KOREA -> KOREAN;
            case NETHERLANDS -> DUTCH;
            case NEPAL -> NEPALI;
            case NORWAY -> NORWEGIAN;
            case GERMANY, AUSTRIA -> GERMAN;
            case USA, AUSTRALIA, UK -> ENGLISH;
            case VIETNAM -> VIETNAMESE;
            case SWEDEN -> SWEDISH;
            case RUSSIA -> RUSSIAN;
            case SPAIN -> SPANISH;
            case ITALY -> ITALIAN;
            case JAPAN -> JAPANESE;
            case CHINA -> CHINESE;
            case FRANCE -> FRENCH;
//            case MONGOLIA -> "Mongolian";
//            case BANGLADESH -> "Bengali";
//            case BELGIUM -> DUTCH, FRENCH, GERMAN;
//            case SWITZERLAND -> GERMAN , FRENCH, ITALIAN;
//            case UZBEKISTAN -> "Uzbek";
//            case INDIA -> "Hindi";
//            case INDONESIA -> "Indonesian";
//            case KAZAKHSTAN -> "Kazakh";
//            case CANADA -> ENGLISH, FRENCH;
//            case THAILAND -> "Thai";
//            case PAKISTAN -> "Urdu";
//            case PHILIPPINES -> "Filipino";
            default -> null;
        };

        Map<Languages, Integer> languageLevels = member.getLanguages().entrySet().stream()
                .filter(entry -> !entry.getKey().equals(nativeLang))  // 모국어 제외
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return FriendResponseDTO.FriendDetailDTO.builder()
                .userId(member.getUserId())
                .name(formatName(member))
                .profileImage(member.getProfileImage())
                .bio(member.getBio())
                .university(member.getUniv().toString())
                .age(member.calculateAge())
                .country(member.getCountry().toString())
                .languageLevels(languageLevels)
                .isFriend(isFriend)
                .hobbyList(hobbyNames)
                .build();
    }

    public FriendResponseDTO.FriendSearchResultDTO toFriendSearchResultDTO(Member member) {
        return FriendResponseDTO.FriendSearchResultDTO.builder()
                .userId(member.getUserId())
                .name(formatName(member))
                .profileImage(member.getProfileImage())
                .build();
    }

    public FriendResponseDTO.FriendRecommendationDTO toFriendRecommendationDTO(
            Member candidate,
            List<FriendResponseDTO.FriendRecommendationDTO.MatchedLanguageDTO> matchedLanguages
    ) {
        return FriendResponseDTO.FriendRecommendationDTO.builder()
                .userId(candidate.getUserId())
                .name(formatName(candidate))
                .profileImage(candidate.getProfileImage())
                .matchedLanguages(matchedLanguages)
                .totalMatchedLanguages(matchedLanguages.size())
                .build();
    }


    private String formatName(Member member) {
        switch (member.getCountry()) {
            case KOREA:
            case CHINA:
            case JAPAN:
                return member.getLastName() + member.getFirstName();
            default:
                return member.getFirstName() + " " + member.getLastName();
        }
    }

}
