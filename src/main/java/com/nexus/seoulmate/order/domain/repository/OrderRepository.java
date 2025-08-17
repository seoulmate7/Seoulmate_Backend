package com.nexus.seoulmate.order.domain.repository;

import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.order.domain.Order;
import com.nexus.seoulmate.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderUid(String orderUid);

    // 실제 검증 때 사용할 상세 로딩용 메서드
    @Query("""
     select o
           from Order o
             join fetch o.meeting m
             join fetch m.userId h
             join fetch o.member p
           where o.merchantUid = :merchantUid
    """)
    Optional<Order> findDetailByMerchantUid(@Param("merchantUid") String merchantUid);

    // 특정 회원이 특정 모임에 paid 상태인가
    boolean existsByMemberAndMeetingAndStatus(Member member, Meeting meeting, OrderStatus status);

    // 결제 상태가 성공(success)인 유저만 조회
    @Query("SELECT o.member FROM Order o " +
            "WHERE o.meeting.id = :meetingId AND o.status = 'PAID'")
    List<Member> findPaidMembersByMeetingId(@Param("meetingId") Long meetingId);
}
