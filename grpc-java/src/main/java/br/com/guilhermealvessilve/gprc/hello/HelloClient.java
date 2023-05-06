package br.com.guilhermealvessilve.gprc.hello;

import br.com.proto.teste.TesteServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloClient {

    public static void main(String[] args) {
        LOG.info("Iniciando chamado do cliente gRPC");

        var channel = ManagedChannelBuilder.forAddress("localhost", HelloServer.PORT)
                .usePlaintext()
                .build(); // ativa o SSL

        LOG.info("Criando stub");

        var client = TesteServiceGrpc.newBlockingStub(channel);

        LOG.info("Desligando canal");
        channel.shutdown();
    }
}
