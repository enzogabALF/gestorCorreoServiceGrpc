package com.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new GestorCorreosServiceImpl())
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("** Apagando el servidor gRPC porque JVM está cerrando");
            if (server != null) {
                server.shutdown();
            }
            System.err.println("** Servidor gRPC detenido");
        }));

        logger.info("Servidor gRPC iniciado en el puerto 50051");
        server.start();
        server.awaitTermination();
    }
}
