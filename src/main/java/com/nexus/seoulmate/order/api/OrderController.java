package com.nexus.seoulmate.order.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.exception.status.SuccessStatus;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.order.api.dto.response.CreateOrderResDto;
import com.nexus.seoulmate.order.application.OrderService;
import com.nexus.seoulmate.order.domain.Order;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;

    @Operation(summary = "주문 생성 API")
    @PostMapping("/{meetingId}")
    public ResponseEntity<Response<CreateOrderResDto>> createOrder(
            @PathVariable Long meetingId,
            @RequestHeader("userId") Long userId // 로그인 구현 후 수정 예정
    ){
        Order order = orderService.createOrder(meetingId, userId);
        CreateOrderResDto resDto = new CreateOrderResDto(
                order.getOrderUid(),
                order.getMerchantUid(),
                order.getAmount());

        return ResponseEntity
                .status(SuccessStatus.CREATE_ORDER.getStatus())
                .body(Response.success(SuccessStatus.CREATE_ORDER, resDto));
    }

    @Operation(summary = "주문 단건 조회 API")
    @GetMapping("/{orderUid}")
    public ResponseEntity<Response<CreateOrderResDto>> getOrder(@PathVariable String orderUid) {
        Order order = orderService.getOrderByUid(orderUid);
        CreateOrderResDto resDto = new CreateOrderResDto(
                order.getOrderUid(),
                order.getMerchantUid(),
                order.getAmount());

        return ResponseEntity
                .ok(Response.success(SuccessStatus.GET_ORDER, resDto));
    }
}
