package com.nexus.seoulmate.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.seoulmate.chat.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate template;
    private final ObjectMapper om;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String body    = new String(message.getBody());

            MessageDTO.Sent dto = om.readValue(body, MessageDTO.Sent.class);
            String roomId = channel.substring("room:".length());

            template.convertAndSend("/topic/room." + roomId, dto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
