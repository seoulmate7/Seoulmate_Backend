package com.nexus.seoulmate.order.domain.repository;

import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.order.domain.Order;
import com.nexus.seoulmate.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderUid(String orderUid);

    // 특정 회원이 특정 모임에 paid 상태인가
    boolean existsByMemberAndMeetingAndStatus(Member member, Meeting meeting, OrderStatus status);
}
