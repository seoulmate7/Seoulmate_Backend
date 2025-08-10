package com.nexus.seoulmate.payment.domain;

public enum PaymentStatus {
    READY, // 주문 생성, 결제 대기
    PAID, // 결제 완료
    CANCELLED, // 결제 취소
    FAILED // 실패
}
