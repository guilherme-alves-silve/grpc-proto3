package br.com.guilhermealvessilve.gprc.hello;

import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HelloServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(50_051).build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Solicitado desligamento do servidor. Desligando ...");
            server.shutdown();
            LOG.info("Servidor desligado!");
        }));

        server.awaitTermination();
    }
}
