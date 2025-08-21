package com.nexus.seoulmate.friend.converter;

import com.nexus.seoulmate.friend.domain.entity.FriendRequest;
import com.nexus.seoulmate.friend.domain.entity.FriendRequestStatus;
import com.nexus.seoulmate.friend.domain.entity.Friendship;
import com.nexus.seoulmate.friend.dto.FriendResponseDTO;
import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.Countries;
import com.nexus.seoulmate.member.domain.enums.Languages;
import org.springframework.stereotype.Component;

import java.util.*;
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

    public FriendResponseDTO.FriendDetailDTO toFriendDetailDTO(Member member, String relation) {
        List<String> hobbyNames = member.getHobbies().stream()
                .map(Hobby::getHobbyName)
                .collect(Collectors.toList());

        Set<Languages> nativeLangs = defaultNativeLanguagesByCountry(member.getCountry());

        Map<String, Integer> languageLevels =
                Optional.ofNullable(member.getLanguages()).orElseGet(Collections::emptyMap)
                        .entrySet().stream()
                        .filter(e -> e.getKey() != null)
                        .filter(e -> !nativeLangs.contains(e.getKey()))
                        .collect(Collectors.toMap(
                                e -> e.getKey().name(),
                                Map.Entry::getValue,
                                Integer::max,
                                LinkedHashMap::new
                        ));


        return FriendResponseDTO.FriendDetailDTO.builder()
                .userId(member.getUserId())
                .name(formatName(member))
                .profileImage(member.getProfileImage())
                .bio(member.getBio())
                .university(member.getUniv().toString())
                .age(member.calculateAge())
                .country(member.getCountry().toString())
                .languageLevels(languageLevels)
                .relation(relation)
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

    private Set<Languages> defaultNativeLanguagesByCountry(Countries country) {
        if (country == null) return Collections.emptySet();
        switch (country) {
            case KOREA:       return Set.of(KOREAN);
            case USA:         return Set.of(ENGLISH);
            case CHINA:       return Set.of(CHINESE);
            case JAPAN:       return Set.of(JAPANESE);
            case NETHERLANDS: return Set.of(DUTCH);
            case NEPAL:       return Set.of(NEPALI);
            case NORWAY:      return Set.of(NORWEGIAN);
            case GERMANY:     return Set.of(GERMAN);
            case RUSSIA:      return Set.of(RUSSIAN);
            case MONGOLIA:    return Set.of(MONGOLIAN);
            case BANGLADESH:  return Set.of(BENGALI);
            case VIETNAM:     return Set.of(VIETNAMESE);
            case BELGIUM:     return Set.of(DUTCH, FRENCH, GERMAN);
            case SWEDEN:      return Set.of(SWEDISH);
            case SWITZERLAND: return Set.of(GERMAN, FRENCH, ITALIAN);
            case SPAIN:       return Set.of(SPANISH);
            case UK:          return Set.of(ENGLISH);
            case AUSTRIA:     return Set.of(GERMAN);
            case UZBEKISTAN:  return Set.of(UZBEK);
            case ITALY:       return Set.of(ITALIAN);
            case INDIA:       return Set.of(HINDI); // 필요시 ENGLISH 추가 고려
            case INDONESIA:   return Set.of(INDONESIAN);
            case KAZAKHSTAN:  return Set.of(KAZAKH);
            case CANADA:      return Set.of(ENGLISH, FRENCH);
            case THAILAND:    return Set.of(THAI);
            case PAKISTAN:    return Set.of(URDU);
            case FRANCE:      return Set.of(FRENCH);
            case PHILIPPINES: return Set.of(FILIPINO);
            case AUSTRALIA:   return Set.of(ENGLISH);
            default:          return Collections.emptySet();
        }
    }

    public FriendResponseDTO.HobbyRecommendationDTO toHobbyRecommendationDTO(
            Member candidate,
            List<String> matchedHobbies
    ) {
        return FriendResponseDTO.HobbyRecommendationDTO.builder()
                .userId(candidate.getUserId())
                .name(formatName(candidate))
                .profileImage(candidate.getProfileImage())
                .matchedHobbies(matchedHobbies)
                .totalMatchedHobbies(matchedHobbies.size())
                .build();
    }


}
