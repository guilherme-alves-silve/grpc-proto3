package br.com.guilhermealvessilve.gprc.calculator;

import br.com.proto.calculator.CalculatorRequest;
import br.com.proto.calculator.CalculatorResponse;
import br.com.proto.calculator.CalculatorServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        LOG.info("Request: " + request);
        long sum = Stream.of(
                    Stream.of(request.getNumber()),
                    request.getNumbersList().stream()
                )
                .flatMap(Function.identity())
                .mapToLong(number -> number)
                .sum();
        var response = CalculatorResponse.newBuilder()
                .setSum(sum)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        LOG.info("Response: " + response);
    }
}
