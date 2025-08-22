package com.nexus.seoulmate.order.domain;

import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.payment.domain.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private  Long orderId;

    // 고유 주문 번호 (외부 노출용)
    @Column(name = "order_uid", unique = true, nullable = false)
    private String orderUid;

    @Column(name = "merchant_uid", unique = true, nullable = false)
    private String merchantUid;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createdAt;

    // 주문 상태 created, paid, cancelled
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "amount", nullable = false)
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // 생성
    public static Order create(Member member, Meeting meeting, int amount) {
        String uid = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0,12);
        return Order.builder()
                .orderUid(uid)
                .merchantUid(uid) // orderUid와 동일한 값
                .member(member)
                .meeting(meeting)
                .amount(amount)
                .status(OrderStatus.READY)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 0원 결제시 order, payment 연결
    public void attachPayment(Payment payment) {
        this.payment = payment;
    }

    // 결제 완료 처리
    public void markPaid(){
        this.status = OrderStatus.PAID;
    }

    // 주문 취소 처리 (보류)
    public void markCancelled(){
        this.status = OrderStatus.CANCELLED;
    }

    // 편의 메서드
    @Transient
    public Member getParticipant(){
        return this.member;
    }
}
