package com.nexus.seoulmate.order.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 생성 응답 DTO")
public record CreateOrderResDto(

        @Schema(description = "주문 고유 식별자", example = "order_abc123")
        String orderUid,

        @Schema(description = "아임포트 결제에 사용되는 UID", example = "merchant_abc123")
        String merchantUid,

        @Schema(description = "결제 금액", example = "100")
        int amount

    ) {
}
