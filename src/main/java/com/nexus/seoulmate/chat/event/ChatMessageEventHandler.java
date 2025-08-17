package com.nexus.seoulmate.chat.event;

import com.nexus.seoulmate.chat.redis.ChatPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatMessageEventHandler {
    private final ChatPublisher chatPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageSaved(ChatEvents.MessageSaved e) {
        chatPublisher.publishToRoom(e.roomId(), e.payload());
    }
}
