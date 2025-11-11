package org.mohyla.payments.domain.persistence;

import org.mohyla.payments.domain.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<List<Payment>> findByUserId(Long userId);
}
