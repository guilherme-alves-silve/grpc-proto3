package br.com.guilhermealvessilve.gprc.greeting;

import br.com.proto.greeting.GreetingRequest;
import br.com.proto.greeting.GreetingResponse;
import br.com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class GreetingClient {

    public static void main(String[] args) {
        if (args.length == 0) {
            LOG.warn("Need one argument!");
            //args = new String[]{ "greet" };
            args = new String[]{ "greet_many_times" };
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50_051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet" -> doGreet(channel);
            case "greet_many_times" -> doGreetManyTimes(channel);
            default -> LOG.warn("Invalid parameter: " + args[0]);
        }

        LOG.info("Shutting down!");
        channel.shutdown();
    }

    private static void doGreet(ManagedChannel channel) {
        LOG.info("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder()
                        .setFirstName("John Doe")
                .build());

        LOG.info("Response: " + response.getResult());
    }

    private static void doGreetManyTimes(ManagedChannel channel) {
        LOG.info("Enter doGreetManyTimes");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        Iterator<GreetingResponse> it = stub.greetManyTimes(GreetingRequest.newBuilder()
                .setFirstName("John Doe")
                .build());

        it.forEachRemaining(response -> LOG.info("Response: " + response.getResult()));
    }
}
