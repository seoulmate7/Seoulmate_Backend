package com.nexus.seoulmate.order.application;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.repository.MeetingRepository;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.order.domain.Order;
import com.nexus.seoulmate.order.domain.OrderStatus;
import com.nexus.seoulmate.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MeetingRepository meetingRepository;

    public Order createOrder(Long meetingId, Member member) {
        // 모임 조회
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new CustomException(ErrorStatus.MEETING_NOT_FOUND));

        // 중복 결제 확인
        boolean exists = orderRepository.existsByMemberAndMeetingAndStatus(member, meeting, OrderStatus.PAID);
        if (exists) {
            throw new CustomException(ErrorStatus.ALREADY_PARTICIPATED);
        }

        // 주문 생성
        int amount = meeting.getPrice();
        Order order = Order.create(member, meeting, amount);

        return orderRepository.save(order);
    }

        public Order getOrderByUid(String orderUid){
            return orderRepository.findByOrderUid(orderUid)
                    .orElseThrow(() -> new CustomException(ErrorStatus.ORDER_NOT_FOUND));
        }
    }

