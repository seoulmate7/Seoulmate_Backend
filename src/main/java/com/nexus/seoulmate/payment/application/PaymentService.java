package com.nexus.seoulmate.payment.application;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.global.status.ErrorStatus;
import com.nexus.seoulmate.notification.event.PaymentCaptureEvent;
import com.nexus.seoulmate.order.domain.Order;
import com.nexus.seoulmate.order.domain.OrderStatus;
import com.nexus.seoulmate.order.domain.repository.OrderRepository;
import com.nexus.seoulmate.payment.api.dto.WebhookPayload;
import com.nexus.seoulmate.payment.domain.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.nexus.seoulmate.payment.domain.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.nexus.seoulmate.payment.domain.PaymentStatus.PAID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IamportClient iamportClient;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentRepository paymentRepository;

    // 결제 검증 (클라이언트에서 호출)
    @Transactional
    public void verifyPayment(String impUid, String merchantUid){
        processPaymentValidation(impUid, merchantUid);
    }

    // 웹훅 처리
    @Transactional
    public void handleWebhook(WebhookPayload payload){
        processPaymentValidation(payload.getImp_uid(), payload.getMerchant_uid());
    }

    private void processPaymentValidation(String impUid, String merchantUid){
        try{
            // Payment 우리 도메인이랑 충돌나서 아래처럼 처리
            com.siot.IamportRestClient.response.Payment payment = iamportClient.paymentByImpUid(impUid).getResponse();

            // 주문 조회
            Order order = orderRepository.findDetailByMerchantUid(merchantUid)
                    .orElseThrow(() -> new CustomException(ErrorStatus.ORDER_NOT_FOUND));

            // 0원이면 아임포트 검증 대신 내부 무료 결제 처리
            if(order.getAmount() == 0) {
                processFreePayment(order);
                return;
            }

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

    // 0원 결제 내부 처리
    @Transactional
    public void processFreePayment(Order order){
        // 중복 paid 방지
        if(order.getStatus() == OrderStatus.PAID){
            return;
        }

        // 결제 free 생성
        String imp = "free_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Payment payment = Payment.builder()
                .impUid(imp)
                .merchantUid(order.getMerchantUid())
                .amount(0)
                .status(PAID)
                .payMethod("FREE")
                .paidAt(LocalDateTime.now())
                .order(order)
                .build();
        paymentRepository.save(payment);

        // 주문 PAID 처리
        order.markPaid();
        order.attachPayment(payment);
        orderRepository.save(order);

        // 알림 유료결제랑 동일하게
        Long meetingId = order.getMeeting().getId();
        Long hostId = order.getMeeting().getHost().getUserId();
        String participantName = order.getParticipant().getFirstName();
        eventPublisher.publishEvent(new PaymentCaptureEvent(meetingId, hostId, participantName, PAID));
    }
}
