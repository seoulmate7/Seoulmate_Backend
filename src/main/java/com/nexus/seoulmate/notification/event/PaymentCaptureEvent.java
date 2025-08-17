package com.nexus.seoulmate.notification.event;

import com.nexus.seoulmate.payment.domain.PaymentStatus;

public record PaymentCaptureEvent(
        Long meetingId,
        Long hostId,
        String participantName,
        PaymentStatus status
) {
}
