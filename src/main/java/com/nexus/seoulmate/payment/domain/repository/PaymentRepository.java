package com.nexus.seoulmate.payment.domain.repository;

import com.nexus.seoulmate.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
