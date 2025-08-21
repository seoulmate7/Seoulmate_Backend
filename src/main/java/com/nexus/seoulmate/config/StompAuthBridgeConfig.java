package com.nexus.seoulmate.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

@Configuration
@Slf4j
public class StompAuthBridgeConfig implements WebSocketMessageBrokerConfigurer {
    private static final String WS_AUTH = "WS_AUTH";

    @Override
    public void configureClientInboundChannel(ChannelRegistration reg) {
        reg.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
                Map<String,Object> attrs = acc.getSessionAttributes();
                String sessionId = acc.getSessionId();
                StompCommand cmd = acc.getCommand();

                if (StompCommand.CONNECT.equals(acc.getCommand())) {
                    log.info("[WS-AUTH] CONNECT received (simpSessionId={})", sessionId);
                    if (attrs != null) {
                        // Spring Security가 세션에 넣은 컨텍스트 키
                        SecurityContext ctx = (SecurityContext) attrs.get("SPRING_SECURITY_CONTEXT");
                        if (ctx != null) {
                            Authentication auth = ctx.getAuthentication();
                            if (auth != null) {
                                // STOMP 세션 사용자로 심기
                                acc.setUser(auth);
                                // (선택) 쓰레드 로컬 컨텍스트에도 심기
                                SecurityContextHolder.getContext().setAuthentication(auth);
                                log.info("[WS-AUTH] principal bound (name={}, authorities={}, simpSessionId={})",
                                        auth.getName(), auth.getAuthorities(), sessionId);
                            }
                        }
                    }
                }else {
                    Authentication auth = null;
                    var user = acc.getUser();

                    if (user instanceof Authentication a) {
                        log.info("[WS-AUTH] acc.getUser() hit: {}", a.getName());
                        auth = a;
                    } else if (attrs != null) {
                        // 1순위: 우리가 캐시해둔 WS_AUTH
                        Object cached = attrs.get(WS_AUTH);
                        if (cached instanceof Authentication a1) {
                            log.debug("[WS-AUTH] restored from WS_AUTH cache: {}", a1.getName());
                            auth = a1;
                        }

                        // 2순위: 원본 SecurityContext
                        if (auth == null) {
                            SecurityContext ctx = (SecurityContext) attrs.get(
                                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                            if (ctx != null && ctx.getAuthentication() != null) {
                                log.debug("[WS-AUTH] restored from SPRING_SECURITY_CONTEXT: {}", ctx.getAuthentication().getName());
                                auth = ctx.getAuthentication();
                            }
                        }

                        // 복구에 성공했다면 프레임에도 심어두면 이후 acc.getUser()가 채워짐
                        if (auth != null) {
                            acc.setUser(auth);
                        }
                    }

                    if (auth != null) {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.info("[WS-AUTH] rebind success (frame={}, principal={})", cmd, auth.getName());
                    } else {
                        log.warn("[WS-AUTH] missing auth on {} frame; acc.user={}, attrs={}",
                                cmd, acc.getUser(), attrs != null ? attrs.keySet() : "null");
                    }
                }
                return message;
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                SecurityContextHolder.clearContext();
            }
        });
    }
}
