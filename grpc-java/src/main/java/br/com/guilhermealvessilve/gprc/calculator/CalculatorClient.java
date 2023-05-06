package br.com.guilhermealvessilve.gprc.calculator;

import br.com.proto.calculator.SumRequest;
import br.com.proto.calculator.SumResponse;
import br.com.proto.calculator.CalculatorServiceGrpc;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CalculatorClient {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50_051)
                .usePlaintext()
                .build();

        doSumFuture(executor, channel);
        doSumBlocking(channel);

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
        LOG.info("Server response blocking: " + response);
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
                LOG.info("Server response non blocking: " + response);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException ex) {
                LOG.error("Error: ", ex);
            }
        }, executor);
    }
}
