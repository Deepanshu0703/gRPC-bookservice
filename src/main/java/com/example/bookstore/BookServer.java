package com.example.bookstore;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BookServer {
    private final Server server;

    public BookServer(int port) {
        this.server = ServerBuilder.forPort(port)
                .addService(new BookServiceImpl())
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on " + server.getPort());

        // Add a shutdown hook to stop the server when the JVM is shutting down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("*** Shutting down gRPC server since JVM is shutting down");
            BookServer.this.stop();
            System.out.println("*** Server shut down");
        }));
    }


    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        BookServer server = new BookServer(50051);
        server.start();
        server.blockUntilShutdown();
    }
}
