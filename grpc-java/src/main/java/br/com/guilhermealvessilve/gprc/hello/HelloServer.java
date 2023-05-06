package br.com.guilhermealvessilve.gprc.hello;

import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HelloServer {

    public static final int PORT = 50_051;

    public static void main(String[] args) throws IOException, InterruptedException {
        LOG.info("Iniciando servidor gRPC");
        var server = ServerBuilder.forPort(PORT).build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Solicitado desligamento do servidor. Desligando ...");
            server.shutdown();
            LOG.info("Servidor desligado!");
        }));

        server.awaitTermination();
    }
}
