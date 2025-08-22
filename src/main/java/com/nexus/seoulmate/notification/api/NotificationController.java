package com.nexus.seoulmate.notification.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.notification.api.dto.response.NotificationRes;
import com.nexus.seoulmate.notification.application.NotificationPushService;
import com.nexus.seoulmate.notification.application.NotificationService;
import com.nexus.seoulmate.notification.domain.LinkTargetType;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

    private final NotificationPushService notificationPushService;
    private final NotificationService notificationService;

    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "id", "title", "updatedAt", "isRead");

    // SSE 구독
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(){
        return notificationPushService.subscribeForCurrentUser();
    }

    // 내 알림 목록 조회
    @GetMapping("/me")
    public ResponseEntity<Response<Page<NotificationRes>>> getMyNotifications(
            @RequestParam(value = "type", required = false)LinkTargetType type,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ){

        // 정렬 조건 검사
        Sort whitelisted = pageable.getSort().stream()
                .filter(o -> ALLOWED_SORTS.contains(o.getProperty()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.isEmpty() ? Sort.by(Sort.Direction.DESC, "createdAt") : Sort.by(list)
                ));

        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), whitelisted);

        // type이 null 이면 디폴트로 모임으로 처리
        Page<NotificationRes> page = notificationService.findAllForCurrentUser(type, safePageable);
        return ResponseEntity.ok(Response.success(SuccessStatus.NOTIFICATION_LIST_OK, page));
    }

    // 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Response<Void>> markRead(@PathVariable Long notificationId
    ){
        notificationService.markReadForCurrentUser(notificationId);
        return ResponseEntity.ok(Response.success(SuccessStatus.NOTIFICATION_READ_OK, null));
    }
}
