package com.nexus.seoulmate.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.seoulmate.chat.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatPublisher {
    private final StringRedisTemplate rt;
    private final ObjectMapper om;

    public void publishToRoom(long roomId, MessageDTO.Sent dto) {
        try {
            String json = om.writeValueAsString(dto);
            rt.convertAndSend("room:" + roomId, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
