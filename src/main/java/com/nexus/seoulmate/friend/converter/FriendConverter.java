package com.nexus.seoulmate.friend.converter;

import com.nexus.seoulmate.friend.domain.entity.FriendRequest;
import com.nexus.seoulmate.friend.domain.entity.FriendRequestStatus;
import com.nexus.seoulmate.friend.domain.entity.Friendship;
import com.nexus.seoulmate.friend.dto.FriendResponseDTO;
import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .build();
    }

    public FriendResponseDTO.FriendDetailDTO toFriendDetailDTO(Member member, boolean isFriend) {
        List<String> hobbyNames = member.getHobbies().stream()
                .map(Hobby::getHobbyName)
                .collect(Collectors.toList());

        String nativeLang = switch (member.getCountry()) {
            case KOREA -> "Korean";
            case NETHERLANDS -> "Dutch";
            case NEPAL -> "Nepali";
            case NORWAY -> "Norwegian";
            case GERMANY, AUSTRIA -> "German";
            case RUSSIA -> "Russian";
            case MONGOLIA -> "Mongolian";
            case USA, AUSTRALIA, UK -> "English";
            case BANGLADESH -> "Bengali";
            case VIETNAM -> "Vietnamese";
            case BELGIUM -> "Dutch,French,German";
            case SWEDEN -> "Swedish";
            case SWITZERLAND -> "German,French,Italian";
            case SPAIN -> "Spanish";
            case UZBEKISTAN -> "Uzbek";
            case ITALY -> "Italian";
            case INDIA -> "Hindi";
            case INDONESIA -> "Indonesian";
            case JAPAN -> "Japanese";
            case CHINA -> "Chinese";
            case KAZAKHSTAN -> "Kazakh";
            case CANADA -> "English,French";
            case THAILAND -> "Thai";
            case PAKISTAN -> "Urdu";
            case FRANCE -> "French";
            case PHILIPPINES -> "Filipino";
            default -> "";
        };

        Map<String, Integer> languageLevels = member.getLanguages().entrySet().stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase(nativeLang))  // 모국어 제외
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return FriendResponseDTO.FriendDetailDTO.builder()
                .userId(member.getUserId())
                .name(member.getFirstName() + " " + member.getLastName())
                .profileImage(member.getProfileImage())
                .bio(member.getBio())
                .university(member.getUniv().toString())
                .age(member.calculateAge())
                .country(member.getCountry().toString())
                .languageLevels(languageLevels)
                .isFriend(isFriend)
                .chemistry(0)
                .hobbyList(hobbyNames)
                .build();
    }

}
