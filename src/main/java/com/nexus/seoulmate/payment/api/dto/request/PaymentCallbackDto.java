package com.nexus.seoulmate.payment.api.dto.request;

public record PaymentCallbackDto(
        String imp_uid,
        String merchant_uid,
        String status // "paid", "failed", "cancelled"
) {
}
