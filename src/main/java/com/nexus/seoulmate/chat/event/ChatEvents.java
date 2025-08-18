package com.nexus.seoulmate.chat.event;

import com.nexus.seoulmate.chat.dto.MessageDTO;

public class ChatEvents {
    public record MessageSaved(Long roomId, MessageDTO.Sent payload) {}
}
