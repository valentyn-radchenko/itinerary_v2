package org.mohyla.itinerary.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PaymentClient {

    @GrpcClient("payments-service")
    private PaymentServiceGrpc.PaymentServiceStub asyncStub;
    @GrpcClient("payments-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub blockingStub;


    public void streamPaymentsForUser(long userId) {
        PaymentRequest request = PaymentRequest.newBuilder()
                .setUserId(userId)
                .build();

        asyncStub.streamPayments(request, new StreamObserver<>() {
            @Override
            public void onNext(PaymentResponse response) {
                log.info("Received payment: {} ({}) {} at {}",
                        response.getDescription(),
                        response.getStatus(),
                        response.getAmount(),
                        response.getTimestamp()
                );
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error receiving payments stream: {}", t.getMessage(), t);
            }

            @Override
            public void onCompleted() {
                log.info("All payments received for user {}", userId);
            }
        });
    }

    public void getPaymentDetails(long id){
        PaymentIdRequest request = PaymentIdRequest.newBuilder()
                .setPaymentId(id).build();

        PaymentResponse response = blockingStub.getPaymentById(request);
        log.info("Retrieved payment via blocking stub: {}", response.getDescription());

    }
}