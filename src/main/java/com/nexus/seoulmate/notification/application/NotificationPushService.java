package com.nexus.seoulmate.notification.application;

import com.nexus.seoulmate.notification.api.dto.NotificationPushDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationPushService {

    private static final long TIMEOUT_MS = 60 * 60 * 1000L; // SSE 연결 타임아웃 1시간
    private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    // 클라이언트가 /notifications/stream으로 접속
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emitters.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(emitter);

        // 연결 종료, 타임아웃, 에러 시 정리
        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        return emitter;
    }

    // 새 알림 도착 시 해당 유저에게 브로드캐스트
    public void pushNew(Long userId, NotificationPushDto dto) {
        broadcast(userId, "notification", dto);
    }

    // 읽음 시 다른 탭 이벤트 상태 동기화
    public void pushRead(Long userId, Long notificationId) {
        broadcast(userId, "notification:read", notificationId);
    }

    // 브로드캐스트
    private void broadcast(Long userId, String event, Object data) {
        Set<SseEmitter> emitterSet = emitters.get(userId);
        if (emitterSet == null) return; // 해당 유저 연결 없으면 종료
        for (SseEmitter emitter : emitterSet){
            try {
                emitter.send(SseEmitter.event()
                        .name(event)
                        .id(nowId())
                        .data(data));
            } catch (IOException e) {
                remove(userId, emitter);
            }
        }
    }

    // emitter 제거
    private void remove(Long userId, SseEmitter emitter) {
        Set<SseEmitter> emitterSet = emitters.get(userId);
        if (emitterSet != null) {
            emitterSet.remove(emitter);
            if (emitterSet.isEmpty()) emitters.remove(userId);
        }
    }

    // 현재 시간을 이벤트 id로 사용
    private String nowId() {
        return String.valueOf(Instant.now().toEpochMilli());
    }

    // 프론트 구독 -> subscribe()에서 SseEmitter(연결) 생성 -> 알림, 읽음 이벤트 전송
}

