package org.mohyla.payments.domain.persistence;

import org.mohyla.payments.domain.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
