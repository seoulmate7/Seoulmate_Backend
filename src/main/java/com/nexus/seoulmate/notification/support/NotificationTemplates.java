package com.nexus.seoulmate.notification.support;

public final class NotificationTemplates {
    private NotificationTemplates() {}

    // 결제 (호스트만 알림)
    public static String meetingJoinedTitle(){
        return "내 모임에 참여했습니다.";
    }
    public static String meetingJoinedContent(String name){
        return name + " 님이 내 모임에 참여했습니다.";
    }

    // 친구 신청
    public static String friendRequestedTitle(){
        return "친구 신청이 왔습니다.";
    }
    public static String friendRequestedContent(String name){
        return name + " 님이 친구 신청을 걸었습니다.";
    }

    // 친구 수락
    public static String friendAcceptedTitle(){
        return "친구 신청을 수락했습니다.";
    }
    public static String friendAcceptedContent(String name){
        return name + " 님이 친구 신청을 수락했습니다.";
    }
}
