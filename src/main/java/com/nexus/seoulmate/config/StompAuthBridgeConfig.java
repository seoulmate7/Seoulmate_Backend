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
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

@Configuration
@Slf4j
public class StompAuthBridgeConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureClientInboundChannel(ChannelRegistration reg) {
        reg.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
                String sessionId = acc.getSessionId();
                StompCommand cmd = acc.getCommand();

                if (StompCommand.CONNECT.equals(acc.getCommand())) {
                    log.info("[WS-AUTH] CONNECT received (simpSessionId={})", sessionId);
                    Map<String,Object> attrs = acc.getSessionAttributes();
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
                    var user = acc.getUser();
                    if (user instanceof Authentication auth) {
                        SecurityContextHolder.getContext().setAuthentication(auth);
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
