package br.com.guilhermealvessilve.gprc.blog;

import com.mongodb.client.MongoClients;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BlogServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50_051;
        LOG.info("Starting server on the port: " + port);

        var client = MongoClients.create("mongodb://root:root@localhost:27017/");

        var server = ServerBuilder.forPort(port)
                .addService(new BlogServiceImpl(client))
                .addService(ProtoReflectionService.newInstance())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.warn("Shutting down server!");
            server.shutdown();
            client.close();
            LOG.warn("Server was shutdown!");
        }));

        server.awaitTermination();
    }
}
