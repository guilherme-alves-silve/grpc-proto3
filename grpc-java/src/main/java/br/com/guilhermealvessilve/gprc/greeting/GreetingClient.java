package br.com.guilhermealvessilve.gprc.greeting;

import br.com.proto.greeting.GreetingRequest;
import br.com.proto.greeting.GreetingResponse;
import br.com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GreetingClient {

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            LOG.warn("Need one argument!");
            //args = new String[]{ "greet" };
            //args = new String[]{ "greet_many_times" };
            //args = new String[]{ "long_greet" };
            args = new String[]{ "greet_everyone" };
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50_051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet" -> doGreet(channel);
            case "greet_many_times" -> doGreetManyTimes(channel);
            case "long_greet" -> doLongGreet(channel);
            case "greet_everyone" -> doGreetEveryone(channel);
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

    private static void doLongGreet(ManagedChannel channel) throws InterruptedException {
        LOG.info("Enter doLongGreet");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        var names = new ArrayList<String>();
        Collections.addAll(names, "John Wick", "Guilherme", "Marie", "Larissa");
        var latch = new CountDownLatch(1);
        StreamObserver<GreetingRequest> requestObserver = stub.longGreet(new StreamObserver<>() {
            @Override
            public void onNext(GreetingResponse response) {
                LOG.info("Response: {}", response.getResult());
            }

            @Override
            public void onError(Throwable th) {
                LOG.error("Error: ", th);
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for (var name : names) {
            requestObserver.onNext(GreetingRequest.newBuilder()
                            .setFirstName(name)
                            .build());
        }

        requestObserver.onCompleted();

        if(!latch.await(3, TimeUnit.SECONDS)) {
            LOG.warn("Not finished on time!");
        }
    }

    private static void doGreetEveryone(ManagedChannel channel) throws InterruptedException {
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        var names = new ArrayList<String>();
        Collections.addAll(names, "John Wick", "Guilherme", "Marie", "Larissa");
        var latch = new CountDownLatch(1);
        StreamObserver<GreetingRequest> requestObserver = stub.greetEveryone(new StreamObserver<>() {
            @Override
            public void onNext(GreetingResponse response) {
                LOG.info("Response: {}", response.getResult());
            }

            @Override
            public void onError(Throwable th) {
                LOG.error("Server error: ", th);
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        names.forEach(name -> requestObserver.onNext(GreetingRequest.newBuilder()
                .setFirstName(name)
                .build()));
        requestObserver.onCompleted();

        if(!latch.await(3, TimeUnit.SECONDS)) {
            LOG.warn("Not finished on time!");
        }
    }
}
