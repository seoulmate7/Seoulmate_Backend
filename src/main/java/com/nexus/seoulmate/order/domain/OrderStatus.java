package com.nexus.seoulmate.order.domain;

public enum OrderStatus {
    READY, // 주문 생성, 결제 대기
    PAID, // 결제 완료
    CANCELLED // 결제 취소
}
