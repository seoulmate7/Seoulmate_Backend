package com.nexus.seoulmate.payment.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "결제 승인 응답 DTO")
public record PaymentApprovalResDto(
        @Schema(description = "결제 상태", example = "PAID")
        String status,

        @Schema(description = "결제 완료 시각", example = "2025-08-03T12:34:56")
        LocalDateTime paidAt
) {
}
