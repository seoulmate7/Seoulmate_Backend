package com.nexus.seoulmate.payment.domain;

import com.nexus.seoulmate.order.domain.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;


    // 아이엠포트에서 발급하는 고유 결제 번호
    @Column(name = "imp_uid",unique = true, nullable = false)
    private String impUid;

    // 결제 요청 시 사용한 (orderUid와 동일)
    @Column(name = "merchant_uid", nullable = false)
    private String merchantUid;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // 결제 수단
    @Column(name = "pay_method")
    private String payMethod;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}
