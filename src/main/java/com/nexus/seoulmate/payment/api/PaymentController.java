package com.nexus.seoulmate.payment.api;

import com.nexus.seoulmate.exception.Response;
import com.nexus.seoulmate.global.status.ErrorStatus;
import com.nexus.seoulmate.global.status.SuccessStatus;
import com.nexus.seoulmate.order.application.OrderService;
import com.nexus.seoulmate.order.domain.Order;
import com.nexus.seoulmate.payment.api.dto.WebhookPayload;
import com.nexus.seoulmate.payment.api.dto.request.PaymentVerifyRequest;
import com.nexus.seoulmate.payment.application.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Tag(name = "결제")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @Operation(summary = "결제 검증 요청 (유료)")
    @PostMapping
    public ResponseEntity<Response<Void>> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        paymentService.verifyPayment(request.getImpUid(), request.getMerchantUid());

        return ResponseEntity
                .ok(Response.success(SuccessStatus.VERIFY_PAYMENT, null));
    }

    @Operation(summary = "0원 결제 처리")
    @PostMapping("/free/{orderUid}")
    public ResponseEntity<Response<Void>> freePayment(@PathVariable("orderUid") String orderUid) {
        Order order = orderService.getOrderByUid(orderUid);
        paymentService.processFreePayment(order); // 중복 방지 처리
        return ResponseEntity.ok(Response.success(SuccessStatus.PAYMENT_SUCCESS, null));
    }

    @Operation(summary = "결제 결과 페이지")
    @GetMapping("/result")
    public ResponseEntity<Response<Void>> paymentResult(@RequestParam("status") String status) {
        if("success".equalsIgnoreCase(status)) {
            return ResponseEntity
                    .status(SuccessStatus.PAYMENT_SUCCESS.getStatus())
                    .body(Response.success(SuccessStatus.PAYMENT_SUCCESS, null));
        } else {
            return ResponseEntity
                    .status(ErrorStatus.PAYMENT_FAILED.getStatus())
                    .body(Response.fail(ErrorStatus.PAYMENT_FAILED));
        }
    }


    @Operation(summary = "웹훅 수신 API (아임포트에서 서버로)")
    @PostMapping("/webhook")
    public ResponseEntity<Response<Void>> handleWebhook(@RequestBody WebhookPayload payload) {
        System.out.println("웹훅 수신: imp_uid = " + payload.getImp_uid());

        paymentService.handleWebhook(payload);

        return ResponseEntity
                .ok(Response.success(SuccessStatus.PAYMENT_WEBHOOK_RECEIVED, null));
    }

}
