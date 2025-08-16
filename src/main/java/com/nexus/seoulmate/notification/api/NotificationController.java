package com.nexus.seoulmate.notification.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.notification.api.dto.response.NotificationRes;
import com.nexus.seoulmate.notification.application.NotificationPushService;
import com.nexus.seoulmate.notification.application.NotificationService;
import com.nexus.seoulmate.notification.domain.LinkTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationPushService notificationPushService;
    private final NotificationService notificationService;

    // SSE 구독
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestHeader("userId") Long userId){
        return notificationPushService.subscribe(userId);
    }

    // 내 알림 목록 조회
    @GetMapping("/me")
    public ResponseEntity<Response<Page<NotificationRes>>> getMyNotifications(
            @RequestHeader("userId") Long userId,
            @RequestParam(value = "type", required = false)LinkTargetType type,
            Pageable pageable
            ){
        // type이 null 이면 디폴트로 모임으로 처리
        Page<NotificationRes> page = notificationService.findAll(userId, type, pageable);
        return ResponseEntity.ok(Response.success(SuccessStatus.NOTIFICATION_LIST_OK, page));
    }

    // 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Response<Void>> markRead(
            @RequestHeader("userId") Long userId,
            @PathVariable Long notificationId
    ){
        notificationService.markRead(userId, notificationId);
        return ResponseEntity.ok(Response.success(SuccessStatus.NOTIFICATION_READ_OK, null));
    }
}
