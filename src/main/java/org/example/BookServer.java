package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BookServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new BookServiceImpl())
                .build();

        System.out.println("Server starting...");
        server.start();
        System.out.println("Server started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.shutdown();
            System.out.println("Server shut down.");
        }));

        server.awaitTermination();
    }
}
