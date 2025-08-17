package com.nexus.seoulmate.notification.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(length = 255, nullable = false)
    private String link; // 프론트에서 바로 사용 가능

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private LinkTargetType targetType; // 모임 or 친구

    @Column(nullable = false, updatable = false)
    private Long targetId; // 모임 or 친구 ID

    @Column(nullable = false)
    private boolean isRead; // 기본 false

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 링크 생성 (targetType + targetId)
    private String buildLink(){
        if(targetType == null || targetId == null) {
            return null;
        }
        return switch (targetType){
            case MEETING -> "/meetings/" + targetId;
            case FRIEND -> "/friends/" + targetId;
        };
    }

    public static Notification initWithTarget(
            Long receiverId,
            String title,
            String message,
            LinkTargetType targetType,
            Long targetId
    ){
        Notification notification = new Notification();
        notification.receiverId = receiverId;
        notification.title = title;
        notification.message = message;
        notification.targetType = targetType;
        notification.targetId = targetId;
        notification.isRead = false;
        notification.createdAt = LocalDateTime.now();
        notification.updatedAt = LocalDateTime.now();
        notification.link = notification.buildLink();
        return notification;
    }

    // 읽음 처리
    public void markRead(){
        if(!this.isRead) {
            this.isRead = true;
            touch();
        }
    }

    // 생성 전 기본값 세팅
    @PrePersist
    private void prePersist(){
        if(this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if(this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if(this.link == null || this.link.isBlank()) {
            this.link = buildLink();
        }
    }

    // update 전 updatedAt 갱신
    @PreUpdate
    private void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    // updateAt만 수동 갱신 (읽음 처리)
    private void touch(){
        this.updatedAt = LocalDateTime.now();
    }
}
