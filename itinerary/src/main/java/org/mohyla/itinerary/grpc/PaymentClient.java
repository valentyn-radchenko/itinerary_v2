package org.mohyla.itinerary.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


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
                System.out.printf(
                        "Received payment: %s (%s) %.2f at %s%n",
                        response.getDescription(),
                        response.getStatus(),
                        response.getAmount(),
                        response.getTimestamp()
                );
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error receiving payments: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("All payments received for user " + userId);
            }
        });
    }

    public void getPaymentDetails(long id){
        PaymentIdRequest request = PaymentIdRequest.newBuilder()
                .setPaymentId(id).build();

        PaymentResponse response = blockingStub.getPaymentById(request);
        System.out.println("BLocking stub: got payment " + response.getDescription());

    }
}