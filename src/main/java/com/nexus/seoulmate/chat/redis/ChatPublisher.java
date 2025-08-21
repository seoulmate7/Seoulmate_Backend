package com.nexus.seoulmate.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.seoulmate.chat.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatPublisher {
    private final StringRedisTemplate rt;
    private final ObjectMapper om;

    public void publishToRoom(long roomId, MessageDTO.Sent dto) {
        try {
            String json = om.writeValueAsString(dto);
            log.info("[REDIS] PUBLISH channel=room:{} bytes={}", roomId, json.length());
            rt.convertAndSend("room:" + roomId, json);
        } catch (Exception e) {
            log.error("[REDIS] publish error roomId=" + roomId, e);
            throw new RuntimeException(e);
        }
    }
}
