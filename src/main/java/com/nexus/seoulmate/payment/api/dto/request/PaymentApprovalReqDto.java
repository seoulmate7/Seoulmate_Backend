package com.nexus.seoulmate.payment.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 승인 요청 DTO")
public record PaymentApprovalReqDto(
        @Schema(description = "결제 고유 UID", example = "imp_0123456789")
        String impUid,

        @Schema(description = "주문 고유 UID", example = "order_abc123")
        String orderUid
) {
}
