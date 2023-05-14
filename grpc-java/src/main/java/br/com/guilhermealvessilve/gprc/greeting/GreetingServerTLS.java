package br.com.guilhermealvessilve.gprc.greeting;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class GreetingServerTLS {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50_051;
        Server server = ServerBuilder.forPort(port)
                .useTransportSecurity(
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem")
                )
                .addService(new GreetingServiceImpl())
                .build();
        server.start();

        LOG.info("Server started on port: " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Received shutdown request!");
            server.shutdown();
            LOG.info("Server stopped!");
        }));

        server.awaitTermination();
    }
}
