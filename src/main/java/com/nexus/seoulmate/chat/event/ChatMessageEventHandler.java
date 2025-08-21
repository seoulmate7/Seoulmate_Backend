package com.nexus.seoulmate.chat.event;

import com.nexus.seoulmate.chat.redis.ChatPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageEventHandler {
    private final ChatPublisher chatPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageSaved(ChatEvents.MessageSaved e) {
        log.info("[EVENT] AFTER_COMMIT enter roomId={} sender={} type={}",
                e.roomId(),
                e.payload() != null ? e.payload().getSenderName() : null,
                e.payload() != null ? e.payload().getType() : null);
        chatPublisher.publishToRoom(e.roomId(), e.payload());
    }
}
