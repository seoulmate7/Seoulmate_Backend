package com.nexus.seoulmate.chat.application;

import com.nexus.seoulmate.chat.converter.ChatConverter;
import com.nexus.seoulmate.chat.domain.entity.*;
import com.nexus.seoulmate.chat.domain.repository.ChatRoomMemberRepository;
import com.nexus.seoulmate.chat.domain.repository.ChatRoomRepository;
import com.nexus.seoulmate.chat.domain.repository.MessageRepository;
import com.nexus.seoulmate.chat.dto.ChatRoomDTO;
import com.nexus.seoulmate.chat.dto.MessageDTO;
import com.nexus.seoulmate.chat.event.ChatEvents;
import com.nexus.seoulmate.chat.redis.ChatPublisher;
import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ChatConverter chatConverter;
    private final ChatPublisher chatPublisher;
    private final ApplicationEventPublisher publisher;
    private final MeetingRepository meetingRepository;

    @Override
    @Transactional
    public ChatRoomDTO.RoomSummary createDirectRoom(ChatRoomDTO.DirectCreateRequest req) {
        Member member = memberService.getCurrentUser();
        Long meId = member.getUserId();

        Long partnerId = req.getPartnerUserId();
        if (partnerId == null || partnerId <= 0 || partnerId.equals(meId)) {
            throw new CustomException(ErrorStatus.CHAT_INVALID_PARTNER);
        }

        Member partner = memberRepository.findById(partnerId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        String partnerFullName = chatConverter.formatName(partner);

        ChatRoom room = ChatRoom.builder()
                .type(RoomType.DIRECT)
                .title(partnerFullName)
                .chatImage(partner.getProfileImage())
                .build();
        chatRoomRepository.save(room);

        chatRoomMemberRepository.save(ChatRoomMember.builder()
                .roomId(room.getId()).userId(meId).role(Role.PARTICIPANT).build());
        chatRoomMemberRepository.save(ChatRoomMember.builder()
                .roomId(room.getId()).userId(partnerId).role(Role.PARTICIPANT).build());

        List<ChatRoomMember> members = chatRoomMemberRepository.findByRoomId(room.getId());
        Map<Long, Member> userMap = memberRepository.findAllById(
                members.stream().map(ChatRoomMember::getUserId).toList()
        ).stream().collect(Collectors.toMap(Member::getUserId, m -> m));

        List<ChatRoomDTO.Participant> participants = members.stream()
                .map(cm -> chatConverter.toParticipant(
                        userMap.get(cm.getUserId()),
                        cm.getUserId(),
                        cm.getRole().name(),
                        cm.getUserId().equals(meId)
                ))
                .toList();

        return chatConverter.toRoomSummaryDirect(room, meId, participants);
    }

    @Override
    @Transactional
    public ChatRoomDTO.RoomSummary createGroupRoom(ChatRoomDTO.GroupCreateRequest req) {
        Member me = memberService.getCurrentUser();
        Long meId = me.getUserId();

        if (req.getMeetingId() == null) {
            throw new CustomException(ErrorStatus.CHAT_GROUP_MEETING_REQUIRED);
        }
        if (req.getMemberUserIds() == null || req.getMemberUserIds().isEmpty()) {
            throw new CustomException(ErrorStatus.INVALID_PARAMETER);
        }

        Set<Long> otherIds = new HashSet<>(req.getMemberUserIds());
        otherIds.remove(meId);

        if (otherIds.size() + 1 < 3) {
            throw new CustomException(ErrorStatus.CHAT_GROUP_MIN_MEMBERS);
        }

        for (Long uid : otherIds) {
            if (uid == null || uid <= 0 || !memberRepository.existsById(uid)) {
                throw new CustomException(ErrorStatus.USER_NOT_FOUND);
            }
        }

        Meeting meeting = meetingRepository.findWithUserById(req.getMeetingId())
                .orElseThrow(() -> new CustomException(ErrorStatus.CHAT_GROUP_MEETING_NOT_FOUND));

        if (meeting.getMeetingType() != MeetingType.PRIVATE) {
            throw new CustomException(ErrorStatus.INVALID_MEETING_TYPE);
        }

        final String title = meeting.getTitle();
        final String image = meeting.getImage();

        ChatRoom room = ChatRoom.builder()
                .type(RoomType.GROUP)
                .title(title)
                .chatImage(image)
                .build();
        chatRoomRepository.save(room);

        List<ChatRoomMember> members = new ArrayList<>();
        members.add(ChatRoomMember.builder()
                .roomId(room.getId())
                .userId(meId)
                .role(Role.OWNER)
                .build());
        for (Long uid : otherIds) {
            members.add(ChatRoomMember.builder()
                    .roomId(room.getId())
                    .userId(uid)
                    .role(Role.PARTICIPANT)
                    .build());
        }
        chatRoomMemberRepository.saveAll(members);

        List<Long> allUserIds = members.stream()
                .map(ChatRoomMember::getUserId)
                .toList();
        List<Member> userEntities = memberRepository.findAllById(allUserIds);
        Map<Long, Member> userMap = userEntities.stream()
                .collect(Collectors.toMap(Member::getUserId, m -> m));

        List<ChatRoomDTO.Participant> participants = members.stream()
                .map(cm -> {
                    Member user = userMap.get(cm.getUserId());
                    return chatConverter.toParticipant(
                            user,
                            cm.getUserId(),
                            cm.getRole().name(),
                            cm.getUserId().equals(meId)
                    );
                })
                .toList();

        return chatConverter.toRoomSummaryGroup(room, meId, participants);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDTO.RoomListItem> getMyRooms(String type, int page, int size) {
        Member me = memberService.getCurrentUser();
        Long meId = me.getUserId();

        RoomType filter = null;
        if (type != null && !type.isBlank()) {
            try {
                filter = RoomType.valueOf(type.trim().toUpperCase()); // DIRECT | GROUP
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorStatus.INVALID_PARAMETER);
            }
        }

        var pageReq = PageRequest.of(page, Math.min(Math.max(size, 1), 20));
        var roomsPage = chatRoomRepository.findMyRoomsOrderByLatestMessage(meId, filter, pageReq);
        List<ChatRoom> rooms = roomsPage.getContent();
        if (rooms.isEmpty()) return List.of();

        List<Long> roomIds = rooms.stream().map(ChatRoom::getId).toList();
        Map<Long, Message> latestByRoom = messageRepository.findLatestByRoomIds(roomIds)
                .stream()
                .collect(Collectors.toMap(Message::getRoomId, m -> m));

        Map<Long, Long> partnerIdByRoom = new HashMap<>();
        var directIds = rooms.stream()
                .filter(r -> r.getType() == RoomType.DIRECT)
                .map(ChatRoom::getId)
                .toList();

        if (!directIds.isEmpty()) {
            var members = chatRoomMemberRepository.findByRoomIdIn(directIds);
            var grouped = members.stream().collect(Collectors.groupingBy(ChatRoomMember::getRoomId));
            for (Long rid : directIds) {
                var list = grouped.getOrDefault(rid, List.of());
                list.stream()
                        .filter(mb -> !Objects.equals(mb.getUserId(), meId))
                        .findFirst()
                        .ifPresent(mb -> partnerIdByRoom.put(rid, mb.getUserId()));
            }
        }

        Map<Long, String> nameByUser = new HashMap<>();
        if (!partnerIdByRoom.isEmpty()) {
            List<Long> pIds = new ArrayList<>(new HashSet<>(partnerIdByRoom.values()));
            memberRepository.findAllById(pIds).forEach(u -> {
                String partnerFullName = chatConverter.formatName(u);
                nameByUser.put(u.getUserId(), partnerFullName);
            });
        }

        List<ChatRoomDTO.RoomListItem> results = new ArrayList<>(rooms.size());
        for (ChatRoom r : rooms) {
            Message latest = latestByRoom.get(r.getId());
            if (r.getType() == RoomType.GROUP) {
                results.add(chatConverter.toListItemGroup(r, latest));
            } else {
                Long pid = partnerIdByRoom.get(r.getId());
                String pname = pid != null ? nameByUser.get(pid) : null;
                results.add(chatConverter.toListItemDirect(r, pid, pname, latest));
            }
        }

        return results;
    }

    @Transactional
    public MessageDTO.Sent sendMessage(Long roomId, MessageDTO.SendRequest req) {
        Member me = memberService.getCurrentUser();
        Long meId = me.getUserId();
        String senderName = chatConverter.formatName(me);;

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        if (!chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, meId)) {
            throw new CustomException(ErrorStatus.CHAT_NOT_MEMBER);
        }

        if (req.getType() == MsgType.TEXT &&
                (req.getContent() == null || req.getContent().isBlank())) {
            throw new CustomException(ErrorStatus.CHAT_EMPTY_MESSAGE);
        }

        Message message = messageRepository.save(
                Message.builder()
                        .roomId(roomId)
                        .senderId(meId)
                        .type(req.getType())
                        .content(req.getContent())
                        .build()
        );
        MessageDTO.Sent sent = chatConverter.toSent(message, senderName);

        publisher.publishEvent(new ChatEvents.MessageSaved(roomId, sent));

        return sent;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDTO.Page getMessages(Long roomId, Long cursor, int size) {
        Member me = memberService.getCurrentUser();
        Long meId = me.getUserId();

        // 권한 체크
        if (!chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, meId)) {
            throw new CustomException(ErrorStatus.CHAT_NOT_MEMBER);
        }

        // 최신부터 size개 가져옴 (cursor 없으면 최신 N개)
        var pageable = PageRequest.of(0, Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "id"));

        List<Message> desc = messageRepository.findRecent(roomId, cursor, pageable);
        if (desc.isEmpty()) {
            return chatConverter.toMessagePage(List.of(), null, false);
        }

        // 발신자 정보 일괄 조회
        List<Long> senderIds = desc.stream().map(Message::getSenderId)
                .distinct().collect(Collectors.toList());
        Map<Long, Member> senderMap = memberRepository.findAllById(senderIds).stream()
                .collect(Collectors.toMap(m -> m.getUserId(), m -> m));

        // 변환
        List<MessageDTO.MessageItem> items = desc.stream()
                .map(m -> chatConverter.toMessageItem(m, senderMap.get(m.getSenderId()), meId))
                .collect(Collectors.toList());

        // nextCursor = 현재 응답 중 가장 오래된 메시지 id (desc의 마지막)
        Long oldestId = desc.get(desc.size() - 1).getId();
        boolean hasMore = messageRepository.existsByRoomIdAndIdLessThan(roomId, oldestId);

        return chatConverter.toMessagePage(items, oldestId, hasMore);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDTO.RoomHeader getRoomHeader(Long roomId) {
        Member me = memberService.getCurrentUser();
        Long meId = me.getUserId();

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        if (!chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, meId)) {
            throw new CustomException(ErrorStatus.CHAT_NOT_MEMBER);
        }

        List<ChatRoomMember> members = chatRoomMemberRepository.findByRoomId(roomId);
        List<Long> uids = members.stream().map(ChatRoomMember::getUserId).toList();

        Map<Long, Member> users = memberRepository.findAllById(uids).stream()
                .collect(Collectors.toMap(m -> m.getUserId(), m -> m));

        List<ChatRoomDTO.Participant> participants = new ArrayList<>();
        for (ChatRoomMember m : members) {
            Member u = users.get(m.getUserId());
            participants.add(
                    chatConverter.toParticipant(u, m.getUserId(), m.getRole().name(), Objects.equals(m.getUserId(), meId))
            );
        }

        if (room.getType() == RoomType.DIRECT) {
            Long partnerId = members.stream()
                    .map(ChatRoomMember::getUserId)
                    .filter(id -> !Objects.equals(id, meId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorStatus.CHAT_INVALID_PARTNER));
            String partnerName = chatConverter.formatName(users.get(partnerId));
            return chatConverter.toHeaderDirect(room, partnerName, participants);
        } else {
            return chatConverter.toHeaderGroup(room, participants);
        }
    }
}
