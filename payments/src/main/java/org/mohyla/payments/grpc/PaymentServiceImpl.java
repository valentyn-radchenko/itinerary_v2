package org.mohyla.payments.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.mohyla.payments.domain.models.Payment;
import org.mohyla.payments.domain.persistence.PaymentRepository;

import java.util.List;
import java.util.Optional;


@GrpcService
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentRepository repo;

    public PaymentServiceImpl(PaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    public void streamPayments(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {

        Long userId = request.getUserId();
        try {
            Optional<List<Payment>> paymentsOpt = repo.findByUserId(userId);

            if (paymentsOpt.isEmpty() || paymentsOpt.get().isEmpty()) {
                responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription("No payments found for user ID: " + userId)
                                .asRuntimeException()
                );
                return;
            }

            List<Payment> payments = paymentsOpt.get();
            for (Payment payment : payments) {
                PaymentResponse paymentResponse = PaymentResponse.newBuilder()
                        .setId(payment.getId())
                        .setUserId(payment.getUserId())
                        .setAmount(payment.getAmount())
                        .setDescription(payment.getDescription())
                        .setPaymentMethod(payment.getPaymentMethod())
                        .setTimestamp(payment.getTimestamp().toString())
                        .setStatus(payment.getStatus())
                        .build();

                responseObserver.onNext(paymentResponse);
            }

            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Server error: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }

    }

    @Override
    public void getPaymentById(PaymentIdRequest request, StreamObserver<PaymentResponse> responseObserver) {

        Long id = request.getPaymentId();
        try {

            Optional<Payment> paymentOpt = repo.findById(id);
            if (paymentOpt.isEmpty()) {
                responseObserver.onError(
                        Status.NOT_FOUND.withDescription("No payment found with id " + id)
                                .asRuntimeException()
                );
                return;
            }
            Payment payment = paymentOpt.get();

            PaymentResponse response = PaymentResponse.newBuilder()
                    .setId(payment.getId())
                    .setUserId(payment.getUserId())
                    .setAmount(payment.getAmount())
                    .setDescription(payment.getDescription())
                    .setPaymentMethod(payment.getPaymentMethod())
                    .setTimestamp(payment.getTimestamp().toString())
                    .setStatus(payment.getStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch (RuntimeException e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Server error: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}

