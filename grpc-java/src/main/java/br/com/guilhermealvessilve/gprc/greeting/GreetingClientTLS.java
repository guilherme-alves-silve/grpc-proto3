package br.com.guilhermealvessilve.gprc.greeting;

import br.com.proto.greeting.GreetingRequest;
import br.com.proto.greeting.GreetingResponse;
import br.com.proto.greeting.GreetingServiceGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GreetingClientTLS {

    private static final int NO_OPTION_SELECTED = -1;

    @SneakyThrows
    public static void main(String[] args) {
        final var options = new String[]{
                "greet",
                "greet_many_times",
                "long_greet",
                "greet_everyone",
                "greet_with_deadline"
        };
        int option = 0;

        if (args.length == 0) {
            if (option >= 0 && option < options.length) {
                args = new String[]{ options[option] };
            } else {
                LOG.warn("Need too select of the following options: {}", Arrays.toString(options));
                return;
            }
        }

        ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .trustManager(new File("ssl/ca.crt"))
                .build();
        ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 50_051, credentials)
                .build();

        switch (args[0]) {
            case "greet" -> doGreet(channel);
            case "greet_many_times" -> doGreetManyTimes(channel);
            case "long_greet" -> doLongGreet(channel);
            case "greet_everyone" -> doGreetEveryone(channel);
            case "greet_with_deadline" -> doGreetWithDeadline(channel);
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

    private static void doGreetWithDeadline(ManagedChannel channel) {
        LOG.info("Enter doGreetWithDeadline");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.withDeadline(Deadline.after(3, TimeUnit.SECONDS))
                .greetWithDeadline(GreetingRequest.newBuilder()
                .setFirstName("John Doe")
                .build());

        LOG.info("Response: " + response.getResult());

        try {
            var responseDeadline = stub.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetingRequest.newBuilder()
                    .setFirstName("John Doe")
                    .build());
            LOG.warn("Greeting deadline exceeded: {}", responseDeadline.getResult()); //Must never happen
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                LOG.error("Deadline has been exceeded");
            } else {
                LOG.error("Got an exception in greetWithDeadline: ", ex);
            }
        }
    }
}
