package com.nexus.seoulmate.payment.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentVerifyRequest {
    private String merchantUid;
    private String impUid;
}
