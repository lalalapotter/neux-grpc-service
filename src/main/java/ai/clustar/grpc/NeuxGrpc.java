package ai.clustar.grpc;

import ai.clustar.grpc.analyzer.AnalyzerReply;
import ai.clustar.grpc.analyzer.AnalyzerRequest;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import io.grpc.BindableService;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;

import java.sql.SQLException;

public class NeuxGrpc {
    public static final String SERVICE_NAME = "neux.Analyzer";
    public static final MethodDescriptor<AnalyzerRequest, AnalyzerReply> METHOD_ANALYZER;
//    public static final MethodDescriptor<ExchangeRequest, ExchangeReply> METHOD_EXCHANGE;
    private static final int METHODID_ANALYZER = 0;
    
    private NeuxGrpc() {
    }

    public static NeuxStub newStub(Channel channel) {
        return new NeuxStub(channel);
    }

    public static NeuxBlockingStub newBlockingStub(Channel channel) {
        return new NeuxBlockingStub(channel);
    }

    public static ServiceDescriptor getServiceDescriptor() {
        return new ServiceDescriptor("neux.Analyzer", new MethodDescriptor[]{METHOD_ANALYZER});
    }

    public static ServerServiceDefinition bindService(HeartBeat serviceImpl) {
        return ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(METHOD_ANALYZER, ServerCalls.asyncUnaryCall(new NeuxGrpc.MethodHandlers(serviceImpl, 0))).build();
    }

    static {
        METHOD_ANALYZER = MethodDescriptor.create(MethodDescriptor.MethodType.UNARY, MethodDescriptor.generateFullMethodName("neux.Analyzer", "selectUsers"), ProtoUtils.marshaller(AnalyzerRequest.getDefaultInstance()), ProtoUtils.marshaller(AnalyzerReply.getDefaultInstance()));
    }

    private static class MethodHandlers<Req, Resp> implements ServerCalls.UnaryMethod<Req, Resp>, ServerCalls.ServerStreamingMethod<Req, Resp>, ServerCalls.ClientStreamingMethod<Req, Resp>, ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final HeartBeat serviceImpl;
        private final int methodId;

        public MethodHandlers(HeartBeat serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        public void invoke(Req request, StreamObserver<Resp> responseObserver) {
            switch(this.methodId) {
                case 0:
                    try {
                        this.serviceImpl.selectUsers((AnalyzerRequest)request, (StreamObserver<AnalyzerReply>) responseObserver);
                    } catch (SQLException | ClassNotFoundException throwables) {
                        throwables.printStackTrace();
                    }
                    return;
                default:
                    throw new AssertionError();
            }
        }

        public StreamObserver<Req> invoke(StreamObserver<Resp> responseObserver) {
            switch(this.methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    public static class NeuxBlockingStub extends AbstractStub<NeuxBlockingStub> implements NeuxBlockingClient {
        private NeuxBlockingStub(Channel channel) {
            super(channel);
        }

        private NeuxBlockingStub(Channel channel, CallOptions callOptions) {
            super(channel, callOptions);
        }

        protected NeuxBlockingStub build(Channel channel, CallOptions callOptions) {
            return new NeuxBlockingStub(channel, callOptions);
        }

        public AnalyzerReply selectUsers(AnalyzerRequest request) {
            return ClientCalls.blockingUnaryCall(this.getChannel(), METHOD_ANALYZER, this.getCallOptions(), request);
        }
    }

    public static class NeuxStub extends AbstractStub<NeuxStub> implements HeartBeat {
        private NeuxStub(Channel channel) {
            super(channel);
        }

        private NeuxStub(Channel channel, CallOptions callOptions) {
            super(channel, callOptions);
        }

        protected NeuxStub build(Channel channel, CallOptions callOptions) {
            return new NeuxStub(channel, callOptions);
        }

        public void selectUsers(AnalyzerRequest request, StreamObserver<AnalyzerReply> responseObserver) {
            ClientCalls.asyncUnaryCall(this.getChannel().newCall(METHOD_ANALYZER, this.getCallOptions()), request, responseObserver);
        }
    }

    public abstract static class GrpcImplBase implements HeartBeat, BindableService {
        public GrpcImplBase() {
        }

        public void selectUsers(AnalyzerRequest request, StreamObserver<AnalyzerReply> responseObserver) throws SQLException, ClassNotFoundException {
            ServerCalls.asyncUnimplementedUnaryCall(METHOD_ANALYZER, responseObserver);
        }

        public ServerServiceDefinition bindService() {
            return NeuxGrpc.bindService(this);
        }
    }

    public interface HeartBeat {
        void selectUsers(AnalyzerRequest var1, StreamObserver<AnalyzerReply> var2) throws SQLException, ClassNotFoundException;
    }

    public interface NeuxBlockingClient {
        AnalyzerReply selectUsers(AnalyzerRequest var1);
    }

}
