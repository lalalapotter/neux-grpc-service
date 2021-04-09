package ai.clustar.grpc;

import ai.clustar.grpc.analyzer.AnalyzerReply;
import ai.clustar.grpc.analyzer.AnalyzerRequest;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class NeuxCoreClient {
    private static final Logger logger = Logger.getLogger(NeuxCoreClient.class.getName());

    private final NeuxGrpc.NeuxBlockingStub blockingStub;

    public NeuxCoreClient(Channel channel) {
        blockingStub = NeuxGrpc.newBlockingStub(channel);
    }

    public void greet(String name) {
        AnalyzerRequest request = AnalyzerRequest.newBuilder().setName(name).build();
        AnalyzerReply response;
        try {
            response = blockingStub.selectUsers(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        String user = "world";
        String target = "localhost:50051";
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [name [target]]");
                System.err.println("");
                System.err.println("  name    The name you wish to be greeted by. Defaults to " + user);
                System.err.println("  target  The server to connect to. Defaults to " + target);
                System.exit(1);
            }
            user = args[0];
        }
        if (args.length > 1) {
            target = args[1];
        }

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        try {
            NeuxCoreClient client = new NeuxCoreClient(channel);
            client.greet(user);
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
