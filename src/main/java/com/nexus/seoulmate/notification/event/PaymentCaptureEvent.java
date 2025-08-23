package com.nexus.seoulmate.notification.event;

import com.nexus.seoulmate.payment.domain.PaymentStatus;

public record PaymentCaptureEvent(
        Long meetingId,
        Long hostId,
        Long participantId,
        String participantName,
        String participantImageUrl,
        PaymentStatus status
) {
}
