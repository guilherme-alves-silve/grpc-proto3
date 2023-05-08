package br.com.guilhermealvessilve.gprc.greeting;

import br.com.proto.greeting.GreetingRequest;
import br.com.proto.greeting.GreetingResponse;
import br.com.proto.greeting.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.StreamObservers;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(GreetingResponse.newBuilder()
                        .setResult("Hello " + request.getFirstName())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {

        for (int i = 0; i < 10; ++i) {
            GreetingResponse response = GreetingResponse.newBuilder()
                    .setResult(i + " - Hello " + request.getFirstName())
                    .build();
            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> longGreet(StreamObserver<GreetingResponse> responseObserver) {
        var stringBuilderResponse = new StringBuilder();
        return new StreamObserver<>() {
            @Override
            public void onNext(GreetingRequest request) {
                stringBuilderResponse.append("Hello ")
                    .append(request.getFirstName())
                    .append("!\n");
            }

            @Override
            public void onError(Throwable th) {
                responseObserver.onError(th);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(GreetingResponse.newBuilder()
                                .setResult(stringBuilderResponse.toString())
                                .build());
                responseObserver.onCompleted();
            }
        };
    }
}
