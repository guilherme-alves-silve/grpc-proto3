package br.com.guilhermealvessilve.gprc.calculator;

import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50_051;
        LOG.info("Starting server on the port: " + port);

        var server = ServerBuilder.forPort(port)
                .addService(new CalculatorServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.warn("Shutting down server!");
            server.shutdown();
            LOG.warn("Server was shutdown!");
        }));

        server.awaitTermination();
    }
}
