package com.nexus.seoulmate.payment.application;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.exception.status.ErrorStatus;
import com.nexus.seoulmate.notification.event.PaymentCaptureEvent;
import com.nexus.seoulmate.order.domain.Order;
import com.nexus.seoulmate.order.domain.repository.OrderRepository;
import com.nexus.seoulmate.payment.api.dto.WebhookPayload;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.nexus.seoulmate.payment.domain.PaymentStatus.PAID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IamportClient iamportClient;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 결제 검증 (클라이언트에서 호출)
    @Transactional
    public void verifyPayment(String impUid, String merchantUid){
        processPaymentValidation(impUid, merchantUid);
    }

    // 웹훅 처리
    public void handleWebhook(WebhookPayload payload){
        processPaymentValidation(payload.getImp_uid(), payload.getMerchant_uid());
    }

    private void processPaymentValidation(String impUid, String merchantUid){
        try{
            Payment payment = iamportClient.paymentByImpUid(impUid).getResponse();

            // 주문 조회
            Order order = orderRepository.findByOrderUid(merchantUid)
                    .orElseThrow(() -> new CustomException(ErrorStatus.ORDER_NOT_FOUND));

            // 금액 검증
            if(payment.getAmount().intValue() != order.getAmount()){
                throw new CustomException(ErrorStatus.AMOUNT_TAMPERED);
            }

            // 상태 검증 및 업데이트
            if("paid".equals(payment.getStatus())){
                order.markPaid(); // 주문 상태 paid로 변경
                orderRepository.save(order);

                // 이벤트 (커밋 후 알림 저장 및 sse 푸시)
                Long meetingId = order.getMeeting().getId();
                Long hostId = order.getMeeting().getHost().getUserId();
                String participantName = order.getParticipant().getFirstName();

                // 알림 생성, 푸시
                eventPublisher.publishEvent(new PaymentCaptureEvent(meetingId, hostId, participantName, PAID));

            } else {
                throw new CustomException(ErrorStatus.PAYMENT_FAILED);
            }

        } catch (IamportResponseException | IOException e){
            log.error("아임포트 API 호출 오류", e);
            throw new CustomException(ErrorStatus.IAMPORT_ERROR);
        }
    }
}
