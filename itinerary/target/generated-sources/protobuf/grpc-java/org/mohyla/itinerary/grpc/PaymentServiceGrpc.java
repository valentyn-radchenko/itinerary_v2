package org.mohyla.itinerary.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.61.0)",
    comments = "Source: payment.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PaymentServiceGrpc {

  private PaymentServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "PaymentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.mohyla.itinerary.grpc.PaymentRequest,
      org.mohyla.itinerary.grpc.PaymentResponse> getStreamPaymentsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamPayments",
      requestType = org.mohyla.itinerary.grpc.PaymentRequest.class,
      responseType = org.mohyla.itinerary.grpc.PaymentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.mohyla.itinerary.grpc.PaymentRequest,
      org.mohyla.itinerary.grpc.PaymentResponse> getStreamPaymentsMethod() {
    io.grpc.MethodDescriptor<org.mohyla.itinerary.grpc.PaymentRequest, org.mohyla.itinerary.grpc.PaymentResponse> getStreamPaymentsMethod;
    if ((getStreamPaymentsMethod = PaymentServiceGrpc.getStreamPaymentsMethod) == null) {
      synchronized (PaymentServiceGrpc.class) {
        if ((getStreamPaymentsMethod = PaymentServiceGrpc.getStreamPaymentsMethod) == null) {
          PaymentServiceGrpc.getStreamPaymentsMethod = getStreamPaymentsMethod =
              io.grpc.MethodDescriptor.<org.mohyla.itinerary.grpc.PaymentRequest, org.mohyla.itinerary.grpc.PaymentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamPayments"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mohyla.itinerary.grpc.PaymentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mohyla.itinerary.grpc.PaymentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PaymentServiceMethodDescriptorSupplier("StreamPayments"))
              .build();
        }
      }
    }
    return getStreamPaymentsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.mohyla.itinerary.grpc.PaymentIdRequest,
      org.mohyla.itinerary.grpc.PaymentResponse> getGetPaymentByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetPaymentById",
      requestType = org.mohyla.itinerary.grpc.PaymentIdRequest.class,
      responseType = org.mohyla.itinerary.grpc.PaymentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.mohyla.itinerary.grpc.PaymentIdRequest,
      org.mohyla.itinerary.grpc.PaymentResponse> getGetPaymentByIdMethod() {
    io.grpc.MethodDescriptor<org.mohyla.itinerary.grpc.PaymentIdRequest, org.mohyla.itinerary.grpc.PaymentResponse> getGetPaymentByIdMethod;
    if ((getGetPaymentByIdMethod = PaymentServiceGrpc.getGetPaymentByIdMethod) == null) {
      synchronized (PaymentServiceGrpc.class) {
        if ((getGetPaymentByIdMethod = PaymentServiceGrpc.getGetPaymentByIdMethod) == null) {
          PaymentServiceGrpc.getGetPaymentByIdMethod = getGetPaymentByIdMethod =
              io.grpc.MethodDescriptor.<org.mohyla.itinerary.grpc.PaymentIdRequest, org.mohyla.itinerary.grpc.PaymentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetPaymentById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mohyla.itinerary.grpc.PaymentIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mohyla.itinerary.grpc.PaymentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PaymentServiceMethodDescriptorSupplier("GetPaymentById"))
              .build();
        }
      }
    }
    return getGetPaymentByIdMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PaymentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PaymentServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PaymentServiceStub>() {
        @java.lang.Override
        public PaymentServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PaymentServiceStub(channel, callOptions);
        }
      };
    return PaymentServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PaymentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PaymentServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PaymentServiceBlockingStub>() {
        @java.lang.Override
        public PaymentServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PaymentServiceBlockingStub(channel, callOptions);
        }
      };
    return PaymentServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PaymentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PaymentServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PaymentServiceFutureStub>() {
        @java.lang.Override
        public PaymentServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PaymentServiceFutureStub(channel, callOptions);
        }
      };
    return PaymentServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void streamPayments(org.mohyla.itinerary.grpc.PaymentRequest request,
        io.grpc.stub.StreamObserver<org.mohyla.itinerary.grpc.PaymentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStreamPaymentsMethod(), responseObserver);
    }

    /**
     */
    default void getPaymentById(org.mohyla.itinerary.grpc.PaymentIdRequest request,
        io.grpc.stub.StreamObserver<org.mohyla.itinerary.grpc.PaymentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetPaymentByIdMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PaymentService.
   */
  public static abstract class PaymentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PaymentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PaymentService.
   */
  public static final class PaymentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PaymentServiceStub> {
    private PaymentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PaymentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PaymentServiceStub(channel, callOptions);
    }

    /**
     */
    public void streamPayments(org.mohyla.itinerary.grpc.PaymentRequest request,
        io.grpc.stub.StreamObserver<org.mohyla.itinerary.grpc.PaymentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getStreamPaymentsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getPaymentById(org.mohyla.itinerary.grpc.PaymentIdRequest request,
        io.grpc.stub.StreamObserver<org.mohyla.itinerary.grpc.PaymentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetPaymentByIdMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PaymentService.
   */
  public static final class PaymentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PaymentServiceBlockingStub> {
    private PaymentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PaymentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PaymentServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<org.mohyla.itinerary.grpc.PaymentResponse> streamPayments(
        org.mohyla.itinerary.grpc.PaymentRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getStreamPaymentsMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.mohyla.itinerary.grpc.PaymentResponse getPaymentById(org.mohyla.itinerary.grpc.PaymentIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetPaymentByIdMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PaymentService.
   */
  public static final class PaymentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PaymentServiceFutureStub> {
    private PaymentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PaymentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PaymentServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.mohyla.itinerary.grpc.PaymentResponse> getPaymentById(
        org.mohyla.itinerary.grpc.PaymentIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetPaymentByIdMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_STREAM_PAYMENTS = 0;
  private static final int METHODID_GET_PAYMENT_BY_ID = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM_PAYMENTS:
          serviceImpl.streamPayments((org.mohyla.itinerary.grpc.PaymentRequest) request,
              (io.grpc.stub.StreamObserver<org.mohyla.itinerary.grpc.PaymentResponse>) responseObserver);
          break;
        case METHODID_GET_PAYMENT_BY_ID:
          serviceImpl.getPaymentById((org.mohyla.itinerary.grpc.PaymentIdRequest) request,
              (io.grpc.stub.StreamObserver<org.mohyla.itinerary.grpc.PaymentResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getStreamPaymentsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              org.mohyla.itinerary.grpc.PaymentRequest,
              org.mohyla.itinerary.grpc.PaymentResponse>(
                service, METHODID_STREAM_PAYMENTS)))
        .addMethod(
          getGetPaymentByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.mohyla.itinerary.grpc.PaymentIdRequest,
              org.mohyla.itinerary.grpc.PaymentResponse>(
                service, METHODID_GET_PAYMENT_BY_ID)))
        .build();
  }

  private static abstract class PaymentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PaymentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.mohyla.itinerary.grpc.PaymentProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PaymentService");
    }
  }

  private static final class PaymentServiceFileDescriptorSupplier
      extends PaymentServiceBaseDescriptorSupplier {
    PaymentServiceFileDescriptorSupplier() {}
  }

  private static final class PaymentServiceMethodDescriptorSupplier
      extends PaymentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PaymentServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PaymentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PaymentServiceFileDescriptorSupplier())
              .addMethod(getStreamPaymentsMethod())
              .addMethod(getGetPaymentByIdMethod())
              .build();
        }
      }
    }
    return result;
  }
}
