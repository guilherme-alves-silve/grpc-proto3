package br.com.guilhermealvessilve.gprc.calculator;

import br.com.proto.calculator.*;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class CalculatorClient {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50_051)
                .usePlaintext()
                .build();

        doSumFuture(executor, channel);
        doSumBlocking(channel);
        doPrimeBlocking(channel);
        doAvgBlocking(channel);

        executor.shutdown();
        if(!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            LOG.warn("The executor not finished on time!");
        }
        channel.shutdown();
    }

    private static void doSumBlocking(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse response = stub.sum(SumRequest.newBuilder()
                        .setNumber(5)
                        .addNumbers(10)
                        .build());
        LOG.info("Server response blocking (sum): " + response);
    }

    private static void doSumFuture(ExecutorService executor, ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceFutureStub stub = CalculatorServiceGrpc.newFutureStub(channel);
        ListenableFuture<SumResponse> responseListenable = stub.sum(SumRequest.newBuilder()
                .setNumber(10L)
                .addAllNumbers(List.of(3L, 5L, 20L))
                .build());

        responseListenable.addListener(() -> {
            try {
                var response = responseListenable.get();
                LOG.info("Server response non blocking (sum): " + response);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException ex) {
                LOG.error("Error: ", ex);
            }
        }, executor);
    }

    private static void doPrimeBlocking(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.prime(PrimeRequest.newBuilder()
                                .setNumber(120)
                                .build())
                .forEachRemaining(response -> LOG.info("Response (prime): " + response.getPrimeFactor()));
    }

    private static void doAvgBlocking(ManagedChannel channel) throws InterruptedException {
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        var latch = new CountDownLatch(1);
        var numbers = List.of(1, 2, 3, 4);
        StreamObserver<AvgRequest> requestObserver = stub.avg(new StreamObserver<>() {
            @Override
            public void onNext(AvgResponse response) {
                LOG.info("Server response (avg): {}", response.getAvg());
            }

            @Override
            public void onError(Throwable th) {
                LOG.error("Server error (avg): ", th);
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for (var number : numbers) {
            requestObserver.onNext(AvgRequest.newBuilder()
                    .setNumber(number)
                    .build());
        }

        requestObserver.onCompleted();

        latch.await();
    }
}
