package br.com.guilhermealvessilve.gprc.calculator;

import br.com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        LOG.info("Request sum: " + request);
        long sum = Stream.of(
                    Stream.of(request.getNumber()),
                    request.getNumbersList().stream()
                )
                .flatMap(Function.identity())
                .mapToLong(number -> number)
                .sum();
        var response = SumResponse.newBuilder()
                .setSum(sum)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        LOG.info("Response sum: " + response);
    }

    @Override
    public void prime(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver) {
        LOG.info("Request prime: " + request);
        long k = 2;
        long n = request.getNumber();
        while (n > 1) {
            if (n % k == 0) {
                var response = PrimeResponse.newBuilder()
                        .setPrimeFactor(k)
                        .build();
                responseObserver.onNext(response);
                n = n / k;
                LOG.info("Response prime: " + response);
            } else {
                k = k + 1;
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AvgRequest> avg(StreamObserver<AvgResponse> responseObserver) {
        var numbers = new ArrayList<Long>();

        return new StreamObserver<>() {
            @Override
            public void onNext(AvgRequest request) {
                LOG.info("Request avg: " + request);
                numbers.add(request.getNumber());
            }

            @Override
            public void onError(Throwable th) {
                responseObserver.onError(th);
            }

            @Override
            public void onCompleted() {
                double avg = numbers.stream()
                                .mapToLong(number -> number)
                                .average()
                                .orElse(0);
                responseObserver.onNext(AvgResponse.newBuilder()
                                .setAvg(avg)
                                .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<>() {
            long max = Long.MIN_VALUE;

            @Override
            public void onNext(MaxRequest request) {
                var number = request.getNumber();
                if (number > max) {
                    max = number;
                    responseObserver.onNext(MaxResponse.newBuilder()
                            .setMax(max)
                            .build());
                }
            }

            @Override
            public void onError(Throwable th) {
                LOG.error("Error: ", th);
                responseObserver.onError(th);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void sqrt(SqrtRequest request, StreamObserver<SqrtResponse> responseObserver) {
        long number = request.getNumber();
        if (number < 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("The number being sent cannot be negative!")
                            .augmentDescription("Number: " + number)
                            .asRuntimeException());
            return;
        }

        responseObserver.onNext(SqrtResponse.newBuilder()
                .setSqrt(Math.sqrt(number))
                .build());
        responseObserver.onCompleted();
    }
}
