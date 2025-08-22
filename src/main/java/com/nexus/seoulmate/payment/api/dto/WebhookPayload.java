package com.nexus.seoulmate.payment.api.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebhookPayload {

    private String imp_uid;
    private String merchant_uid;

    public WebhookPayload(String imp_uid, String merchant_uid) {
        this.imp_uid = imp_uid;
        this.merchant_uid = merchant_uid;
    }
}
