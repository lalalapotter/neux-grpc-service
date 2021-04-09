package ai.clustar.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import ai.clustar.grpc.analyzer.AnalyzerReply;
import ai.clustar.grpc.analyzer.AnalyzerRequest;
import io.grpc.stub.StreamObserver;
import java.sql.*;
public class NeuxCoreServer {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/neux";
    static final String USER = "root";
    static final String PASS = "password";
    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new GrpcImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    NeuxCoreServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final NeuxCoreServer server = new NeuxCoreServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class GrpcImpl extends NeuxGrpc.GrpcImplBase{
        @Override
        public void selectUsers(AnalyzerRequest req, StreamObserver<AnalyzerReply> responseObserver) throws SQLException, ClassNotFoundException {
            Class.forName(JDBC_DRIVER);
            Connection conn = null;
            Statement stmt = null;
            System.out.println("Connecting ...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String username  = rs.getString("username");
                String password = rs.getString("password");
                int enabled = rs.getInt("enabled");
                System.out.print("Username: " + username);
                System.out.print(", Password: " + password);
                System.out.print(", Enabled: " + enabled);
                System.out.print("\n");
            }
            rs.close();
            stmt.close();
            conn.close();
            AnalyzerReply reply = AnalyzerReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
