package com.nexus.seoulmate.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentResultController {

    @Operation(summary = "결제 성공 안내 페이지", description = "결제 성공 시 보여지는 HTML 화면을 반환합니다.")
    @GetMapping("/success-payment")
    public String successPayment() {
        return "success-payment";
    }

    @Operation(summary = "결제 실패 안내 페이지", description = "결제 실패 시 보여지는 HTML 화면을 반환합니다.")
    @GetMapping("/fail-payment")
    public String failPayment() {
        return "fail-payment";
    }
}
