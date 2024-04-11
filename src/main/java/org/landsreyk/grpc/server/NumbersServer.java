package org.landsreyk.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumbersServer {
    private static final int PORT = 8080;

    @SneakyThrows
    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new NumbersServiceImpl())
                .build();

        log.info("numbers server is starting...");
        server.start();

        // для корректного завершения работы сервера
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Received shutdown request");
            server.shutdown();
            log.info("Server stopped");
        }));

        log.info("server is waiting for client on port={}", PORT);
        server.awaitTermination();
    }
}
